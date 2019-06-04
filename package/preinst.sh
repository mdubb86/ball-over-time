#!/bin/bash
USER=ballovertime

# Create user if necessary
id -u $USER &>/dev/null || useradd -m $USER 

