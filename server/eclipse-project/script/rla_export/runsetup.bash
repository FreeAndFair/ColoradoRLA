#!/bin/bash
# Apply options to python setup commands (like install, sdist)
# for pbr (Python Build Reasonableness) to avoid generating inappropriate
# AUTHORS and ChangeLog files

SKIP_GENERATE_AUTHORS=1 SKIP_WRITE_GIT_CHANGELOG=1 python setup.py "$@"
