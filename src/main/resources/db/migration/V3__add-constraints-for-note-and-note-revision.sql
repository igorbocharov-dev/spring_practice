ALTER TABLE note ADD CONSTRAINT check_title_length CHECK (length(title) >= 5 AND length(title) <= 250);
ALTER TABLE note ADD CONSTRAINT check_text_length CHECK (length(text) >= 1);
ALTER TABLE note ADD CONSTRAINT check_author_length CHECK (length(author) >= 3 AND length(author) <= 100);

ALTER TABLE note_revision ADD CONSTRAINT check_old_title_length CHECK (length(old_title) >= 5 AND length(old_title) <= 250);
ALTER TABLE note_revision ADD CONSTRAINT check_old_text_length CHECK (length(old_text) >= 1);