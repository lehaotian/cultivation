@echo off
chcp 65001 >nul

set server_dir=../server/base/src/main/java
set server_proto_dir=%server_dir%/cls/cn/base/proto

if exist "%server_proto_dir%" (
    echo 清理Java输出目录...
    del /q "%server_proto_dir%"
) else (
    mkdir "%server_proto_dir%"
)

echo 开始将proto文件编译为Java...

%~dp0protoc/bin/protoc.exe -I=../protocol ../protocol/*.proto --java_out=%server_dir%

if errorlevel 1 pause

echo 服务器导协议成功！

