FROM node:18-alpine

WORKDIR /app

# Install CLIs
RUN npm install -g @angular/cli @ionic/cli @capacitor/cli

# Copy package files and install dependencies
COPY package*.json ./
RUN npm install

# Copy everything else
COPY . .

# Serve the application
CMD ["ionic", "serve", "--host", "0.0.0.0", "--port", "8100", "--no-open"]
