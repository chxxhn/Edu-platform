ALTER TABLE Users
    ALTER COLUMN date_created SET DEFAULT NOW();
ALTER TABLE Users
    ALTER COLUMN last_updated SET DEFAULT NOW();

CREATE OR REPLACE FUNCTION update_last_updated_column()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.last_updated = NOW();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_last_updated
    BEFORE UPDATE ON Users
    FOR EACH ROW
EXECUTE FUNCTION update_last_updated_column();

CREATE OR REPLACE FUNCTION set_default_grade()
    RETURNS TRIGGER AS $$
BEGIN
    -- grade가 NULL인 경우 기본값으로 'STD' 설정
    IF NEW.grade IS NULL THEN
        NEW.grade := 'STD';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 트리거를 Users 테이블에 추가
CREATE TRIGGER set_default_grade_trigger
    BEFORE INSERT ON Users
    FOR EACH ROW
EXECUTE FUNCTION set_default_grade();

CREATE OR REPLACE FUNCTION restrict_grade_update()
    RETURNS TRIGGER AS $$
BEGIN
    -- app.current_user_grade가 설정되지 않았거나 TA 또는 PROF가 아닌 경우 예외 발생
    IF current_setting('app.current_user_grade', true) NOT IN ('TA', 'PROF') THEN
        RAISE EXCEPTION '권한이 없습니다: grade를 변경할 수 없습니다.';
    END IF;

    -- 사용자 자신의 grade를 수정하려는 경우
    IF NEW.stdId = current_setting('app.current_user_id', true)::INTEGER THEN
        IF current_setting('app.current_user_grade', true) NOT IN ('TA', 'PROF') THEN
            RAISE EXCEPTION '사용자는 자신의 grade를 수정할 수 없습니다.';
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

ALTER TABLE group_user
    DROP CONSTRAINT fkc6tpqk1plmbxmslbrn2kdm9i9;

ALTER TABLE group_user
    ADD CONSTRAINT fkc6tpqk1plmbxmslbrn2kdm9i9
        FOREIGN KEY (group_id) REFERENCES group_projects(gp_id) ON DELETE CASCADE;


DROP TRIGGER IF EXISTS delete_groupusers_trigger ON group_projects;

CREATE OR REPLACE FUNCTION delete_related_groupusers()
    RETURNS TRIGGER
    LANGUAGE plpgsql
AS $$
BEGIN
    DELETE FROM group_user WHERE group_id = OLD.gp_id;
    RETURN OLD;
END;
$$;

ALTER FUNCTION delete_related_groupusers() OWNER TO "gim-yeseul";

CREATE TRIGGER delete_groupusers_trigger
    AFTER DELETE ON group_projects
    FOR EACH ROW
EXECUTE FUNCTION delete_related_groupusers();


CREATE OR REPLACE FUNCTION insert_into_group_approve()
    RETURNS TRIGGER AS $$
BEGIN
    -- value 값이 true로 변경될 때 동작
    IF NEW.value = TRUE AND OLD.value IS DISTINCT FROM TRUE THEN
        INSERT INTO group_approve (group_id, lecture_id)
        VALUES (NEW.group_id, NEW.lecture_id);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_insert_into_group_approve
    AFTER UPDATE ON group_projects -- value를 변경하는 테이블 이름
    FOR EACH ROW
EXECUTE FUNCTION insert_into_group_approve();
