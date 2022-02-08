使用keytool生成RSA证书jwt.jks，复制到resources目录下。在JDK的bin目录下使用如下命令即可:

keytool -genkey -alias jwt -keyalg RSA -keystore jwt.jks

注意：如果创建过程中设置了秘钥库密码 和 秘钥密码，则需要同步修改apollo配置