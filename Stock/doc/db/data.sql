DROP TABLE IF EXISTS public.trade;
DROP TABLE IF EXISTS public.stock;
DROP TABLE IF EXISTS public.user_application;



CREATE TABLE public.user_application
(
    user_id SERIAL PRIMARY KEY,
	user_name character varying(100)  NOT NULL UNIQUE,
    first_name character varying(50),
    last_name character varying(50),
    email character varying(100),
	phone character varying(100),
	password character varying(1000),
	notes text
  
);


INSERT INTO public.user_application VALUES (1, 'admin','Denni', 'Duffer', 'de@d.com', '444-444-4444',  'yH5J3zwWdbsUl+2nGIIQVPSj85rJV5w0SWfAhKdM8FMW+fZq6H3BuF01S0rsO/KgDZYlxXPoSYXf1rQ4fgGCe9wW+kjn/4Fyv6XqZeiOtDimeyTOXGTVRCkG/ZP+JGDLDrIsmPukCUHn5FUBamPZ8UI6F06hXgIEThVBkM4YOnXIbdNz03H2H4agI1vVXY4kGNWfj1PD3yilemDbFzLjKa108tgNvVa6KMxmQ4deTFbK/YzVkA8ENfew352UmmtGrSdNhKpGAPpJH0gj0z/UuXKvcP7GF2USKCesWjnIbS+NnKAEvQPKwJHdGYQavuk5+Rkx+87vZoOwnvjZnwe+JNrz90HQGjzQ5j7MubN/IXm/3x3xVX9dGSlk7tZGGQRovCQ9i5J2gya3RAtts9wxNJ1FL7s5gylAXYPW5/LmGBMMMdkcB9rCOojar5mRS+o0JqeTu9mKnoSY/qR1q3DriWUCYsYAuCKKEAYeldpcSt51D67yHPZY73Vy24qUa5A/JdVqEkzFBKcgpQqa6rnNa8ILiEKoJDsFxTDOE0mhqqMOgbrE/BFDxDPMPZj53gZReMegdS6Rxy+FIRV77U0Wbz7S0gWJXYFu7B4x/9LY0hiSXOj2Mbtwc7hIH7uA4b7ggm1CHgy7sXnEiiG56EoS9TqkURLPlSiyEhlkzvJxniA=$pEwJIWdODpVHqc/lNEgaoiNAnsaclE+1FEVGSSlMpGHiHZ6tQGQPSXcGGGRf/8fwOW19IU9cR46GgfR2ylVUbg==',NULL);
INSERT INTO public.user_application VALUES (2, 'christine','Christ', 'Mat', 'c2gg.com', '444-444-4444',  '58qFbNlQqI0QuPIICbgrX9Hn6+pS3Q9zc2SNRI4C3ZkWSF6AWT7kD1heuGKb85hi8RkxmfUvT6wplKKZskbHZ4ww9jp+nOoeCbvJoOckntWaWkOjPf3RZriVbVaij+gGMpApo5Poh5NlPdxDcwrxTzd356XP1xBfuDRHz33EBizGKeu/DcOlHsgxq/6LIh2L7+jnnj/0qAfOAd6as0SS7eaHJVkLaH9G3r7H1Ym8oi4YJY8ev7CY5FKjuKuBp0cGO6ghy1hMGy8fW2Hn7biDPk4P/9/LmAJUw990aTE4omLLWdJLuP+VK5p4qFYGLMazIdohTvT462Z6YKYz5/1LWdvamZIPNk3YIWteIuYNDynOlUQi3D1MkbvnQ5fleLXdAFPQw5hGNwSGbjuRrJJPahojXH2FN5KbtYovDfrlUAISlJ0TRs1TXNxn8DxiMNacl53PNO8xCvbIlCyG0CWR2cPQpRfP6EkDI6cl4IGny5RJV5SPVLmmM66F9TDGLLMf8EEldygBri/H7ePd0t40RKs0BhKuAQqoKVYSTM4jQsDNPzn3CauLqtuwH+YF0MRYtN+JbXa85LVYYjwNMxzJVuAKgDmBLoDKhHeS8DfC7C5okiPiFPjWQE7kZC2xiHAIl8Tt0ksIWMkogq+euvXfWqIDiS4RJZDxHFlt9knfFyI=$+YMIE1eK4hb2Ds77IjfNdKzSyVD/MXDoGBCBEUrl2nk3xwMmNLSj5An05chr+z1XJP0h+zsBDoRnbzVMJqaATQ==',NULL);
INSERT INTO public.user_application VALUES (3, 'cow','Holy', 'Cow', 'cow@hoy.com', '444-444-4444', '58qFbNlQqI0QuPIICbgrX9Hn6+pS3Q9zc2SNRI4C3ZkWSF6AWT7kD1heuGKb85hi8RkxmfUvT6wplKKZskbHZ4ww9jp+nOoeCbvJoOckntWaWkOjPf3RZriVbVaij+gGMpApo5Poh5NlPdxDcwrxTzd356XP1xBfuDRHz33EBizGKeu/DcOlHsgxq/6LIh2L7+jnnj/0qAfOAd6as0SS7eaHJVkLaH9G3r7H1Ym8oi4YJY8ev7CY5FKjuKuBp0cGO6ghy1hMGy8fW2Hn7biDPk4P/9/LmAJUw990aTE4omLLWdJLuP+VK5p4qFYGLMazIdohTvT462Z6YKYz5/1LWdvamZIPNk3YIWteIuYNDynOlUQi3D1MkbvnQ5fleLXdAFPQw5hGNwSGbjuRrJJPahojXH2FN5KbtYovDfrlUAISlJ0TRs1TXNxn8DxiMNacl53PNO8xCvbIlCyG0CWR2cPQpRfP6EkDI6cl4IGny5RJV5SPVLmmM66F9TDGLLMf8EEldygBri/H7ePd0t40RKs0BhKuAQqoKVYSTM4jQsDNPzn3CauLqtuwH+YF0MRYtN+JbXa85LVYYjwNMxzJVuAKgDmBLoDKhHeS8DfC7C5okiPiFPjWQE7kZC2xiHAIl8Tt0ksIWMkogq+euvXfWqIDiS4RJZDxHFlt9knfFyI=$+YMIE1eK4hb2Ds77IjfNdKzSyVD/MXDoGBCBEUrl2nk3xwMmNLSj5An05chr+z1XJP0h+zsBDoRnbzVMJqaATQ==',NULL);
INSERT INTO public.user_application VALUES (4, 'raven','Crow', 'raven', 'cow@hoy.com', '444-444-4444',  '58qFbNlQqI0QuPIICbgrX9Hn6+pS3Q9zc2SNRI4C3ZkWSF6AWT7kD1heuGKb85hi8RkxmfUvT6wplKKZskbHZ4ww9jp+nOoeCbvJoOckntWaWkOjPf3RZriVbVaij+gGMpApo5Poh5NlPdxDcwrxTzd356XP1xBfuDRHz33EBizGKeu/DcOlHsgxq/6LIh2L7+jnnj/0qAfOAd6as0SS7eaHJVkLaH9G3r7H1Ym8oi4YJY8ev7CY5FKjuKuBp0cGO6ghy1hMGy8fW2Hn7biDPk4P/9/LmAJUw990aTE4omLLWdJLuP+VK5p4qFYGLMazIdohTvT462Z6YKYz5/1LWdvamZIPNk3YIWteIuYNDynOlUQi3D1MkbvnQ5fleLXdAFPQw5hGNwSGbjuRrJJPahojXH2FN5KbtYovDfrlUAISlJ0TRs1TXNxn8DxiMNacl53PNO8xCvbIlCyG0CWR2cPQpRfP6EkDI6cl4IGny5RJV5SPVLmmM66F9TDGLLMf8EEldygBri/H7ePd0t40RKs0BhKuAQqoKVYSTM4jQsDNPzn3CauLqtuwH+YF0MRYtN+JbXa85LVYYjwNMxzJVuAKgDmBLoDKhHeS8DfC7C5okiPiFPjWQE7kZC2xiHAIl8Tt0ksIWMkogq+euvXfWqIDiS4RJZDxHFlt9knfFyI=$+YMIE1eK4hb2Ds77IjfNdKzSyVD/MXDoGBCBEUrl2nk3xwMmNLSj5An05chr+z1XJP0h+zsBDoRnbzVMJqaATQ==',NULL);
INSERT INTO public.user_application VALUES (5, 'baley','Beetle', 'baley', 'Beetle.bailey@gamail.com', '444-444-4444',  '58qFbNlQqI0QuPIICbgrX9Hn6+pS3Q9zc2SNRI4C3ZkWSF6AWT7kD1heuGKb85hi8RkxmfUvT6wplKKZskbHZ4ww9jp+nOoeCbvJoOckntWaWkOjPf3RZriVbVaij+gGMpApo5Poh5NlPdxDcwrxTzd356XP1xBfuDRHz33EBizGKeu/DcOlHsgxq/6LIh2L7+jnnj/0qAfOAd6as0SS7eaHJVkLaH9G3r7H1Ym8oi4YJY8ev7CY5FKjuKuBp0cGO6ghy1hMGy8fW2Hn7biDPk4P/9/LmAJUw990aTE4omLLWdJLuP+VK5p4qFYGLMazIdohTvT462Z6YKYz5/1LWdvamZIPNk3YIWteIuYNDynOlUQi3D1MkbvnQ5fleLXdAFPQw5hGNwSGbjuRrJJPahojXH2FN5KbtYovDfrlUAISlJ0TRs1TXNxn8DxiMNacl53PNO8xCvbIlCyG0CWR2cPQpRfP6EkDI6cl4IGny5RJV5SPVLmmM66F9TDGLLMf8EEldygBri/H7ePd0t40RKs0BhKuAQqoKVYSTM4jQsDNPzn3CauLqtuwH+YF0MRYtN+JbXa85LVYYjwNMxzJVuAKgDmBLoDKhHeS8DfC7C5okiPiFPjWQE7kZC2xiHAIl8Tt0ksIWMkogq+euvXfWqIDiS4RJZDxHFlt9knfFyI=$+YMIE1eK4hb2Ds77IjfNdKzSyVD/MXDoGBCBEUrl2nk3xwMmNLSj5An05chr+z1XJP0h+zsBDoRnbzVMJqaATQ==',NULL);
INSERT INTO public.user_application VALUES (6, 'bob','Sponge Bob', 'Square Pants', 'oceanfloor@gamail.com', '444-444-4444',  '58qFbNlQqI0QuPIICbgrX9Hn6+pS3Q9zc2SNRI4C3ZkWSF6AWT7kD1heuGKb85hi8RkxmfUvT6wplKKZskbHZ4ww9jp+nOoeCbvJoOckntWaWkOjPf3RZriVbVaij+gGMpApo5Poh5NlPdxDcwrxTzd356XP1xBfuDRHz33EBizGKeu/DcOlHsgxq/6LIh2L7+jnnj/0qAfOAd6as0SS7eaHJVkLaH9G3r7H1Ym8oi4YJY8ev7CY5FKjuKuBp0cGO6ghy1hMGy8fW2Hn7biDPk4P/9/LmAJUw990aTE4omLLWdJLuP+VK5p4qFYGLMazIdohTvT462Z6YKYz5/1LWdvamZIPNk3YIWteIuYNDynOlUQi3D1MkbvnQ5fleLXdAFPQw5hGNwSGbjuRrJJPahojXH2FN5KbtYovDfrlUAISlJ0TRs1TXNxn8DxiMNacl53PNO8xCvbIlCyG0CWR2cPQpRfP6EkDI6cl4IGny5RJV5SPVLmmM66F9TDGLLMf8EEldygBri/H7ePd0t40RKs0BhKuAQqoKVYSTM4jQsDNPzn3CauLqtuwH+YF0MRYtN+JbXa85LVYYjwNMxzJVuAKgDmBLoDKhHeS8DfC7C5okiPiFPjWQE7kZC2xiHAIl8Tt0ksIWMkogq+euvXfWqIDiS4RJZDxHFlt9knfFyI=$+YMIE1eK4hb2Ds77IjfNdKzSyVD/MXDoGBCBEUrl2nk3xwMmNLSj5An05chr+z1XJP0h+zsBDoRnbzVMJqaATQ==',NULL);
INSERT INTO public.user_application VALUES (7, 'catbert','Catbert', 'Evil HR', 'catbert@gamail.com', '444-444-4444', '58qFbNlQqI0QuPIICbgrX9Hn6+pS3Q9zc2SNRI4C3ZkWSF6AWT7kD1heuGKb85hi8RkxmfUvT6wplKKZskbHZ4ww9jp+nOoeCbvJoOckntWaWkOjPf3RZriVbVaij+gGMpApo5Poh5NlPdxDcwrxTzd356XP1xBfuDRHz33EBizGKeu/DcOlHsgxq/6LIh2L7+jnnj/0qAfOAd6as0SS7eaHJVkLaH9G3r7H1Ym8oi4YJY8ev7CY5FKjuKuBp0cGO6ghy1hMGy8fW2Hn7biDPk4P/9/LmAJUw990aTE4omLLWdJLuP+VK5p4qFYGLMazIdohTvT462Z6YKYz5/1LWdvamZIPNk3YIWteIuYNDynOlUQi3D1MkbvnQ5fleLXdAFPQw5hGNwSGbjuRrJJPahojXH2FN5KbtYovDfrlUAISlJ0TRs1TXNxn8DxiMNacl53PNO8xCvbIlCyG0CWR2cPQpRfP6EkDI6cl4IGny5RJV5SPVLmmM66F9TDGLLMf8EEldygBri/H7ePd0t40RKs0BhKuAQqoKVYSTM4jQsDNPzn3CauLqtuwH+YF0MRYtN+JbXa85LVYYjwNMxzJVuAKgDmBLoDKhHeS8DfC7C5okiPiFPjWQE7kZC2xiHAIl8Tt0ksIWMkogq+euvXfWqIDiS4RJZDxHFlt9knfFyI=$+YMIE1eK4hb2Ds77IjfNdKzSyVD/MXDoGBCBEUrl2nk3xwMmNLSj5An05chr+z1XJP0h+zsBDoRnbzVMJqaATQ==',NULL);
INSERT INTO public.user_application VALUES (8, 'dogbert','Dogbert', ' is a dog', 'dogbert@gamail.com', '444-444-4444',  '58qFbNlQqI0QuPIICbgrX9Hn6+pS3Q9zc2SNRI4C3ZkWSF6AWT7kD1heuGKb85hi8RkxmfUvT6wplKKZskbHZ4ww9jp+nOoeCbvJoOckntWaWkOjPf3RZriVbVaij+gGMpApo5Poh5NlPdxDcwrxTzd356XP1xBfuDRHz33EBizGKeu/DcOlHsgxq/6LIh2L7+jnnj/0qAfOAd6as0SS7eaHJVkLaH9G3r7H1Ym8oi4YJY8ev7CY5FKjuKuBp0cGO6ghy1hMGy8fW2Hn7biDPk4P/9/LmAJUw990aTE4omLLWdJLuP+VK5p4qFYGLMazIdohTvT462Z6YKYz5/1LWdvamZIPNk3YIWteIuYNDynOlUQi3D1MkbvnQ5fleLXdAFPQw5hGNwSGbjuRrJJPahojXH2FN5KbtYovDfrlUAISlJ0TRs1TXNxn8DxiMNacl53PNO8xCvbIlCyG0CWR2cPQpRfP6EkDI6cl4IGny5RJV5SPVLmmM66F9TDGLLMf8EEldygBri/H7ePd0t40RKs0BhKuAQqoKVYSTM4jQsDNPzn3CauLqtuwH+YF0MRYtN+JbXa85LVYYjwNMxzJVuAKgDmBLoDKhHeS8DfC7C5okiPiFPjWQE7kZC2xiHAIl8Tt0ksIWMkogq+euvXfWqIDiS4RJZDxHFlt9knfFyI=$+YMIE1eK4hb2Ds77IjfNdKzSyVD/MXDoGBCBEUrl2nk3xwMmNLSj5An05chr+z1XJP0h+zsBDoRnbzVMJqaATQ==',NULL);
INSERT INTO public.user_application VALUES (9, 'wally','Wally', 'Bald head', 'codemonkey@gamail.com', '444-444-4444',  '58qFbNlQqI0QuPIICbgrX9Hn6+pS3Q9zc2SNRI4C3ZkWSF6AWT7kD1heuGKb85hi8RkxmfUvT6wplKKZskbHZ4ww9jp+nOoeCbvJoOckntWaWkOjPf3RZriVbVaij+gGMpApo5Poh5NlPdxDcwrxTzd356XP1xBfuDRHz33EBizGKeu/DcOlHsgxq/6LIh2L7+jnnj/0qAfOAd6as0SS7eaHJVkLaH9G3r7H1Ym8oi4YJY8ev7CY5FKjuKuBp0cGO6ghy1hMGy8fW2Hn7biDPk4P/9/LmAJUw990aTE4omLLWdJLuP+VK5p4qFYGLMazIdohTvT462Z6YKYz5/1LWdvamZIPNk3YIWteIuYNDynOlUQi3D1MkbvnQ5fleLXdAFPQw5hGNwSGbjuRrJJPahojXH2FN5KbtYovDfrlUAISlJ0TRs1TXNxn8DxiMNacl53PNO8xCvbIlCyG0CWR2cPQpRfP6EkDI6cl4IGny5RJV5SPVLmmM66F9TDGLLMf8EEldygBri/H7ePd0t40RKs0BhKuAQqoKVYSTM4jQsDNPzn3CauLqtuwH+YF0MRYtN+JbXa85LVYYjwNMxzJVuAKgDmBLoDKhHeS8DfC7C5okiPiFPjWQE7kZC2xiHAIl8Tt0ksIWMkogq+euvXfWqIDiS4RJZDxHFlt9knfFyI=$+YMIE1eK4hb2Ds77IjfNdKzSyVD/MXDoGBCBEUrl2nk3xwMmNLSj5An05chr+z1XJP0h+zsBDoRnbzVMJqaATQ==',NULL);




SELECT setval(pg_get_serial_sequence('public.user_application', 'user_id'), (SELECT MAX(user_id) FROM public.user_application)+1);


CREATE TABLE public.stock
(
 stock_id SERIAL PRIMARY KEY, 
 user_id bigint,
 stock_symbol VARCHAR(50)  NOT NULL,
 active boolean,
 stock_description text,
 	CONSTRAINT user_id FOREIGN KEY (user_id)
	REFERENCES public.user_application (user_id) MATCH SIMPLE
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
 );
 
 
INSERT INTO public.stock VALUES (0,1,'TEST',TRUE,'TEST');
INSERT INTO public.stock VALUES (1,1,'POU',TRUE,'POU');
INSERT INTO public.stock VALUES (2,1,'SAIL',TRUE,'SAIL');
INSERT INTO public.stock VALUES (3,1,'RCOM',TRUE,'RCOM');
INSERT INTO public.stock VALUES (4,1,'JPASSOCIAT',TRUE,'JPASSOCIAT');


CREATE TABLE public.trade
(
 trade_id SERIAL PRIMARY KEY, 
 user_id bigint,
 stock_id bigint,
 date_trade date,
 stock_price double precision,
 stock_quantity_traded bigint,
 cash_added double precision,
 notes text,
 active boolean,
  
	CONSTRAINT user_id FOREIGN KEY (user_id)
	REFERENCES public.user_application (user_id) MATCH SIMPLE
	ON UPDATE NO ACTION
	ON DELETE NO ACTION,
		CONSTRAINT stock_id FOREIGN KEY (stock_id)
	REFERENCES public.stock (stock_id) MATCH SIMPLE
	ON UPDATE NO ACTION
	ON DELETE NO ACTION
);


INSERT INTO public.trade VALUES (1,1,0,'2021-05-01', 10,500, 22,NULL,TRUE);
INSERT INTO public.trade VALUES (2,1,0,'2021-05-02', 8,75, 19,NULL,TRUE);
INSERT INTO public.trade VALUES (3,1,0,'2021-05-03', 5,427, 10,NULL,TRUE);
INSERT INTO public.trade VALUES (4,1,0,'2021-05-04', 4,490, 2,NULL,TRUE);

INSERT INTO public.trade VALUES (5,1,0,'2021-05-05', 5,0, 2,NULL,TRUE);

INSERT INTO public.trade VALUES (6,1,0,'2021-05-05', 8,-424, 17,NULL,TRUE);
INSERT INTO public.trade VALUES (7,1,0,'2021-05-07',10,-226, 27,NULL,TRUE);
INSERT INTO public.trade VALUES (8,1,0,'2021-05-08',8,0, 27,NULL,TRUE);
INSERT INTO public.trade VALUES (9,1,0,'2021-05-09', 5,543, 15,NULL,TRUE);
INSERT INTO public.trade VALUES (10,1,0,'2021-05-10', 4,653, 3,NULL,TRUE);
INSERT INTO public.trade VALUES (11,1,0,'2021-05-11', 5,0, 3,NULL,TRUE);
INSERT INTO public.trade VALUES (12,1,0,'2021-05-12', 8,-583, 24,NULL,TRUE);


SELECT setval(pg_get_serial_sequence('public.trade', 'trade_id'), (SELECT MAX(trade_id) FROM public.trade)+1);
 
INSERT INTO public.trade(user_id,stock_id,date_trade,stock_price,stock_quantity_traded,cash_added,active) 
VALUES (1,1,'2021-11-11', 24.65,1115, 0,TRUE);

INSERT INTO public.trade(user_id,stock_id,date_trade,stock_price,stock_quantity_traded,cash_added,active) 
VALUES (1,2,'2021-11-11', 114.55,128, 0,TRUE);

INSERT INTO public.trade(user_id,stock_id,date_trade,stock_price,stock_quantity_traded,cash_added,active) 
VALUES (1,3,'2021-11-11', 2.91,10210, 0,TRUE);

INSERT INTO public.trade(user_id,stock_id,date_trade,stock_price,stock_quantity_traded,cash_added,active) 
VALUES (1,4,'2021-11-11', 8.51,1026, 0,TRUE);
INSERT INTO public.trade(user_id,stock_id,date_trade,stock_price,stock_quantity_traded,cash_added,active) 
VALUES (1,4,'2021-11-24', 10.14,-21, 0,TRUE);
 


