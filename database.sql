--
-- PostgreSQL database dump
--

-- Dumped from database version 10.12 (Ubuntu 10.12-0ubuntu0.18.04.1)
-- Dumped by pg_dump version 10.12 (Ubuntu 10.12-0ubuntu0.18.04.1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: messages; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.messages (
    sender integer,
    receiver integer,
    message text,
    "time" bigint,
    sender_nick text
);


ALTER TABLE public.messages OWNER TO postgres;

--
-- Name: user_id_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_id_seq OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.users (
    id integer DEFAULT nextval('public.user_id_seq'::regclass),
    nick text,
    token text
);


ALTER TABLE public.users OWNER TO postgres;

--
-- Name: users_id_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX users_id_idx ON public.users USING btree (id);


--
-- Name: users_nick_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX users_nick_idx ON public.users USING btree (nick);


--
-- Name: users_token_idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX users_token_idx ON public.users USING btree (token);


--
-- PostgreSQL database dump complete
--