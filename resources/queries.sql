-- :name sql-insert-user!
-- :result :returning-execute
-- :doc inserts a user returning the ID
INSERT INTO users (id, data)
VALUES (uuid(:id), jsonb(:data))
RETURNING :id;

-- :name sql-get-user :? :1
-- :doc returns the user with the specified ID
SELECT * FROM users
WHERE id = uuid(:id);