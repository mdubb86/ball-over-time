#!/bin/bash
USER=ballovertime
BASE_DIR=/ballovertime
APP_DIR=/ballovertime/home/$USER/app

mkdir -p $APP_DIR/{bin,conf,logs}

cp /ballovertime.jar $APP_DIR/bin
dpkg-deb --build ballovertime
cp ballovertime.deb /output
