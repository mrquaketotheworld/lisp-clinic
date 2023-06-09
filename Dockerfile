FROM node:18.16.0 AS builder
RUN mkdir -p /build
WORKDIR /build
COPY . .

# install deps
RUN apt update && apt install -y openjdk-17-jre-headless
RUN curl -O https://download.clojure.org/install/linux-install-1.11.1.1273.sh && \
    chmod +x linux-install-1.11.1.1273.sh && \
    ./linux-install-1.11.1.1273.sh

# build cljs
RUN cd cljs && npm ci && npm run build

# build clj
RUN cd clj && clojure -T:build uber

# production step
FROM eclipse-temurin:17-alpine
COPY --from=builder /build/clj/target/lisp-clinic.jar .

# run final build
ENTRYPOINT ["java", "-jar", "lisp-clinic.jar"]
