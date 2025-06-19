#!/bin/bash

cd /data

# Compiling c++ code
g++ main.cpp -o main.out 2> compile_error.txt

# Catch Compilation Error
if [ -s compile_error.txt ]; then
  echo "COMPILATION_ERROR"
  cat compile_error.txt
  exit 1
fi

# Run the code with input and store output
timeout 2s ./main.out < input.txt > output.txt 2> runtime_error.txt

# Check exit status of code execution and catch runtime error
if [ $? -ne 0 ]; then
  echo "RUNTIME_ERROR"
  cat runtime_error.txt
  exit 1
fi

# Return output
cat output.txt

