#!/bin/bash

# Find all test classes in the project
echo "*****    *      "
echo "*        *      "
echo "*               "
echo "*****    *      "
echo "     *   *      "
echo "     *   *      "
echo "*****    *****  "

for test_class in $(find ./src/test/java/com/team/e -name "*Test.java" | sed 's/^.*\/\([A-Za-z0-9_]*Test\)\.java/\1/')
do
    # Run Maven clean test for each test class
    echo "Running test: $test_class"
    mvn clean test -Dtest=$test_class -DfailIfNoTests=false
done
