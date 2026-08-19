# # Giai đoạn 1: Build project bằng Node.js
# FROM node:18-alpine AS build
# WORKDIR /app

# # Copy file định nghĩa các gói phụ thuộc để cài đặt
# COPY package*.json ./
# RUN npm install

# # Copy toàn bộ code vào container
# COPY . .

# # Build dự án (Vite sẽ tạo thư mục 'dist')
# RUN npm run build

# # Giai đoạn 2: Sử dụng Nginx để phục vụ file tĩnh
# FROM nginx:alpine

# # Copy kết quả đã build từ bước trước vào thư mục của Nginx
# COPY --from=build /app/dist /usr/share/nginx/html

# # Mở cổng 80 cho web
# EXPOSE 80
# CMD ["nginx", "-g", "daemon off;"]





# Giai đoạn 1: Build project bằng Node.js
FROM node:22-bookworm-slim AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci --include=optional || npm install --include=optional
COPY . .
RUN npm run build

# Giai đoạn 2: Sử dụng Nginx để phục vụ file tĩnh
FROM nginx:alpine

# Copy kết quả đã build từ bước trước vào thư mục của Nginx
COPY --from=build /app/dist /usr/share/nginx/html

# COPY FILE CẤU HÌNH NGIX VÀO ĐÂY
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Mở cổng 80 cho web
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]