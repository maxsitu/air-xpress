#!/bin/bash

sbt -J-Xdebug -J-Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 "~backend/run"
#sbt "~backend/run"