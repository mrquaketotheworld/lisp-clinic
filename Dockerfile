FROM node:18.16.0
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
RUN rm -rf cljs/node_modules && rm -rf cljs/.shadow-cljs

# build clj
RUN cd clj && clojure -T:build uber

# run final build
ENTRYPOINT ["java", "-jar", "clj/target/lisp-clinic.jar"]
