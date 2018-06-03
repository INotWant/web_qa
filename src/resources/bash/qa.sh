#!/usr/bin/env bash
# TODO 这里写死
docker cp /home/jie/project/java/web_qa/src/resources/text/qe_text qa:/home/QA/data/qe_text
docker exec qa /usr/bin/python /home/QA/application.py /home/QA/data/qe_text > /home/jie/project/java/web_qa/src/resources/result
