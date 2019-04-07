#!/bin/sh 

sudo kill -9 $(sudo ps aux | grep "labiot" | grep -v 'grep' | awk '{print $0}')