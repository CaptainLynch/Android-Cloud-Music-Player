FROM node:20-alpine

RUN apk add --no-cache curl

RUN git clone --depth=1 https://github.com/Binaryify/NeteaseCloudMusicApi.git /app

WORKDIR /app

RUN npm install --production

EXPOSE 3000

CMD ["node", "app.js"]
