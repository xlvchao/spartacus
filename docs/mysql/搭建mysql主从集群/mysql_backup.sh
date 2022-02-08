#!/bin/bash

#1、备份数据库（表结构、表数据）
#docker 容器 ID 删除mysql容器后需要修改变量
ID=mysql-master

#mysql备份文件路径
BACKDIR=/mysql_backup
#时间：年月日
TODAY=`date +%Y%m%d`
#时间：昨天的年月日
YESTERDAY=`date -d yesterday +%Y%m%d`
#进入mysql备份文件路径下
cd ${BACKDIR}
#遍历 backup_src文件夹 读取数据库名 操作
for line in `cat backup_src_dbnames`;do
  echo ${line}
  #备份指定数据库
  docker exec -i ${ID} mysqldump -uroot -pPwd@123 ${line} > ${BACKDIR}/${TODAY}_${line}.sql
  #每一天备份结束，删除前一天备份的文件。
  if [ $? -eq 0 ];then
    if [ -f ${BACKDIR}/${TODAY}_${line}.sql ];then
       find ${BACKDIR} -name ${YESTERDAY}_${line}.sql |xargs rm -rf 
    fi  
  fi  
done


#2、发送邮件
#先检查系统是否自带mail或mailx工具（centos一般自带了这个工具）：mail -V  或 mailx -V
#centos下的配置文件为：/etc/mail.rc
#在文件末尾追加(去除set前的#号)：
#设置发送邮件的邮箱和发邮件名称为cg
#set from=903862218@qq.com
#设置smtp服务器地址
#set smtp=smtp.qq.com:587
#设置邮箱账户
#set smtp-auth-user=903862218@qq.com
#设置密码，为授权码
#set smtp-auth-password=rwerwerwrwrwerwe
#SSL验证信息
#set smtp-auth=login
#set smtp-use-starttls
#set ssl-verify=ignore
#设置证书存放路径
#set nss-config-dir=/etc/pki/nssdb/

#注意：mail发送邮件平常使用25端口，也可以使用587，465(ssl)，587和25端口一样，465端口就是加密端口。平常一些服务器运营商会禁止使用25端口，使得用上面的配置则发送不出去，因此可以使用587端口
#设置使用587端口：set smtp=smtp.qq.com:587

EMAIL_RECIVER="xxx@qq.com"
EMAIL_SUBJECT="备份数据！"
EMAIL_CONTENT="备份数据！"
ATTACH_FILES=""

#遍历 backup_src文件夹 读取数据库名 操作
cd ${BACKDIR}
for line in `cat backup_src_dbnames`;do
  ATTACH_FILES="${ATTACH_FILES} -a ${BACKDIR}/${TODAY}_${line}.sql "
done
echo ${ATTACH_FILES}

echo ${EMAIL_CONTENT} | mail -v -s ${EMAIL_SUBJECT} ${ATTACH_FILES} ${EMAIL_RECIVER}


#3、使用说明
#设置定时任务：
#crontab -e
#0 23 * * * /mysql_backup/mysql_backup.sh
#查看定时任务：
#crontab -l
#恢复数据：
#docker exec -i docker容器ID mysql -uroot -p数据库密码 -D 需要恢复的数据库名 < ./对应的备份好的数据库名.sql