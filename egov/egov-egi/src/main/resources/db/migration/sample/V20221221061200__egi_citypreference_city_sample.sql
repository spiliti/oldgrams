insert into eg_citypreferences (id, municipalitylogo, createdby,createddate, lastmodifiedby, lastmodifieddate, version, municipalityname) values (1,1,1, now(),1,now(), 0, 'Mapepe City Council');
update eg_city set domainurl = 'mapepe2.zm4a.org', name ='Mapepe City Council', preferences = 1, localname = 'Mapepe' where id = 1
update eg_citypreferences set recaptchapk = '6LfidggTAAAAANDSoCgfkNdvYm3Ugnl9HC8_68o0', recaptchapub = '6LfidggTAAAAADwfl4uOq1CSLhCkH8OE7QFinbVs';
