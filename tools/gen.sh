#!/bin/bash

dotnet Luban/Luban.dll ^
    --conf luban.conf ^
    -t all ^
    -c java-json ^
    -c typescript-json ^
    -d json ^
    -i prod