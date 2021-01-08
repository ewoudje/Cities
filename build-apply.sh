#!/bin/bash
cd ~/ewoudje/cities.git

git checkout dev

mvn clean package install

cd target && sftp://plugin-dev.04d3358d:dev@localhost:2022 "put -O plugins cities-dev.jar; bye"