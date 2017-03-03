CREATE TABLE "public"."t_company" (
"term_start" varchar(255),
"term_end" varchar(255),
"check_date" varchar(255),
"company_id" varchar(255),
"company_name" varchar(255),
"serial_no" varchar(255),
"belong_org" varchar(255),
"oper_name" varchar(255),
"start_date" varchar(255),
"end_date" varchar(255),
"status" varchar(255),
"province" varchar(255),
"updated_date" varchar(255),
"credit_code" varchar(255),
"regist_capi" varchar(255),
"econ_kind" varchar(255),
"address" varchar(255),
"scope" varchar(255),
"regist_capi_desc" varchar(255),
"url" varchar(255),
"phone_number" varchar,
"email" varchar(255)
)
WITH (OIDS=FALSE)
;

ALTER TABLE "public"."t_company" OWNER TO "gpadmin";