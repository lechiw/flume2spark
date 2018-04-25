# flume2spark
一，项目说明
    为了落地对flume和spark的学习，也为了进一步深入学习flume和spark，基于以上两点编写了此项目以及文档，
此项目是用flume监听文件夹中文件内容的变化，即模拟日志文件的产生，将新增的内容推送到spark框架进行计算，计算的
产出为在一段时间(30秒)内新增内容中不同单纯的数量详细。

二，环境说明
linux:Red Hat 4.4.7-16
spark:spark-2.3.0-bin-hadoop2.7
flume:apache-flume-1.8.0-bin
jdk:1.8
Maven:构建工具
IDEA:开发工具

三，环境准备
1.修改/etc/hosts文件，将ip映射为机器名，在其中追加上以下内容，其中“171.20.25.81”为当前服务器的IP地址，“DataWorks.Master”可随便取名
# vim /etc/hosts
171.20.25.81   DataWorks.Master
2.安装jdk并设置环境变量

四，安装spark
1.下载spark
打开http://spark.apache.org/downloads.html，下载spark-2.3.0-bin-hadoop2.7.tgz，或者见softwae目录
2.上传spark压缩
将下载好的包上传到/usr/local/spark/目录中
2.解压spark
#cd /usr/local/spark/
#tar -zxvf spark-2.3.0-bin-hadoop2.7.tgz
3.启动spark
见第七步的部署

五，安装flume并配置
1.下载flume
打开http://flume.apache.org/download.html，下载apache-flume-1.8.0-bin.tar.gz文件，或者见softwae目录
2.上次压缩包到服务器
上传apache-flume-1.8.0-bin.tar.gz到/usr/local/flume中
3.解压flume
#cd /usr/local/flume
#tar -zxvf apache-flume-1.8.0-bin.tar.gz
3.更改配置文件
将当前项目resources目录下的flume-conf.properties上传到目录/user/local/flume/apache-flume-1.8.0-bin/conf中
4.启动项目
见第七步

六，编写单词计数代码
1.打包jar
在idea的最右边的“Maven Project”工具拦出点击“install”操作
2.上传jar到服务器
获取到jar后上传到/usr/local/sparkApps 目录下

七，部署
1.新建flume监听目录
#mkdir -p /usr/local/flume/tmp/TestDir
2.启动spark，取名“控制台1”
拷贝resource目录的sparkrun.sh内容到linux命令行，并回车执行
#/usr/local/spark/spark-2.3.0-bin-hadoop2.7/bin/spark-submit --class com.tima.flume2spark.job.FlumePushDate2SparkStreaming --master spark://DataWorks.Master:7077 /usr/local/sparkApps/flume2spark-0.0.1-SNAPSHOT.jar
3.另起一个客户端窗口，启动flume，取名“控制台2”
拷贝resource目录的flumerun.sh内容到linux命令行，并回车执行
#. /usr/local/flume/apache-flume-1.8.0-bin/bin/flume-ng agent -c /usr/local/flume/apache-flume-1.8.0-bin/conf/ -f /usr/local/flume/apache-flume-1.8.0-bin/conf/flume-conf.properties -n agent1 -Dflume.root.logger=INFO,console
4.另起一个客户端窗口，向flume监听的文件目录中追加文件，取名“控制台3”
#cd /usr/local/flume
#vim 111.txt
#hello
#spark
#flume
#wq
#cp 111.txt /usr/local/flume/tmp/TestDir
5.在“控制台1”查看执行结果
