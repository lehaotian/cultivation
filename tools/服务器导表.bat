@echo off
chcp 65001 >nul
dotnet Luban\Luban.dll ^
    --conf luban.conf ^
    -t server ^
    -c java-json ^
    -d json ^
    -i prod ^
    -x json.outputDataDir=../server/.meta ^
    -x java-json.outputCodeDir=../server/base/src/main/java/cls/cn/base/meta

if errorlevel 1 pause

echo 服务器导表成功！