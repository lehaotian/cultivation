@echo off
chcp 65001 >nul
dotnet Luban\Luban.dll ^
    --conf luban.conf ^
    -t client ^
    -c typescript-json ^
    -d json ^
    -i prod ^
    -x json.outputDataDir=../client/.meta ^
    -x typescript-json.outputCodeDir=../client/clientCode

if errorlevel 1 pause

echo 客户端导表成功！