@echo off
chcp 65001 >nul

set client_proto_dir=..\client\proto

if exist "%client_proto_dir%" (
    echo 清理TypeScript输出目录...
    del /q "%client_proto_dir%"
) else (
    mkdir "%client_proto_dir%"
)

echo 开始将proto文件编译为TypeScript...

%~dp0protoc/bin/protoc.exe -I=../protocol ../protocol/*.proto --ts_out=%client_proto_dir%

if errorlevel 1 pause

echo 客户端导协议成功！

