-- insert into eg_filestoremap (id, filestoreid, filename, contenttype, version, createddate, lastmodifieddate, lastmodifiedby, createdby)  values (1, 1, 'mapepe_logo.png', 'image',  1, now(), now(), 1, 1);
update eg_city set districtname='Mapepe', districtcode='001', grade='City' where code='0001';
update eg_city set domainurl = '41.63.0.133', name ='Mapepe City Council',  localname = 'Mapepe', latitude = -15.5702365, longitude = 28.2702036 where code = '0001';

insert into eg_citypreferences (id, municipalitylogo, createdby,createddate, lastmodifiedby, lastmodifieddate, version, municipalityname) values (nextval('seq_eg_citypreferences'),null,1, now(),1,now(), 0, 'Mapepe City Council');

update eg_city set preferences = 1; 

update eg_citypreferences set recaptchapk = '6LfidggTAAAAANDSoCgfkNdvYm3Ugnl9HC8_68o0', recaptchapub = '6LfidggTAAAAADwfl4uOq1CSLhCkH8OE7QFinbVs';
