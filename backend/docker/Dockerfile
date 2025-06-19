FROM gcc:12

# Specify working directory
WORKDIR /app

# Copy run.sh bash file into docker's working directory
COPY run.sh .

# Grant execution permission
RUN chmod +x run.sh

# Run bash file
CMD ["./run.sh"]