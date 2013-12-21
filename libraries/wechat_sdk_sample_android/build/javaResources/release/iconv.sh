#!/bin/sh


for i in `find . -iname "*.java"`
do
	echo iconv -f gb2312 -t  utf-8 ${i} >${i}.utf8
	iconv -f gb2312 -t  utf-8 ${i} >${i}.utf8
	echo mv ${i}.utf8 ${i}
	mv ${i}.utf8 ${i}

done

