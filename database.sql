CREATE TABLE messages (
    sender integer,
    receiver integer,
    message text,
    "time" bigint
);

CREATE SEQUENCE user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE users (
    id integer DEFAULT nextval('user_id_seq'::regclass),
    nick text,
    token text
);

CREATE INDEX users_id_idx ON public.users USING btree (id);

CREATE INDEX users_nick_idx ON public.users USING btree (nick);

CREATE INDEX users_token_idx ON public.users USING btree (token);
