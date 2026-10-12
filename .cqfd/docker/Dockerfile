FROM ubuntu:24.04

ENV DEBIAN_FRONTEND=noninteractive

# Yocto host requirements (see yocto-docs ref-manual/system-requirements.rst)
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    chrpath \
    cpio \
    debianutils \
    diffstat \
    file \
    gawk \
    gcc \
    git \
    iputils-ping \
    libacl1 \
    liblz4-tool \
    locales \
    make \
    python3 \
    python3-git \
    python3-jinja2 \
    python3-pexpect \
    python3-pip \
    python3-subunit \
    python3-websockets \
    rpcsvc-proto \
    screen \
    socat \
    sudo \
    texinfo \
    tmux \
    unzip \
    wget \
    xz-utils \
    zstd \
    && rm -rf /var/lib/apt/lists/*

RUN locale-gen en_US.UTF-8
ENV LANG=en_US.UTF-8
ENV LC_ALL=en_US.UTF-8

RUN pip3 install --break-system-packages kas==5.5

# Cocogitto for commit linting, changelogs, and release bumps
ARG COG_VERSION=7.0.0
ARG COG_SHA256=e03938ff2c4c86d71c00c0f3284dbbe95c5ca76fe34a51f33e945c23010d59bb
RUN wget -q -O /tmp/cog.tar.gz \
    "https://github.com/cocogitto/cocogitto/releases/download/${COG_VERSION}/cocogitto-${COG_VERSION}-x86_64-unknown-linux-musl.tar.gz" \
    && echo "${COG_SHA256}  /tmp/cog.tar.gz" | sha256sum --check --strict \
    && tar -xzf /tmp/cog.tar.gz -C /usr/local/bin --strip-components=1 \
       x86_64-unknown-linux-musl/cog \
    && rm /tmp/cog.tar.gz \
    && cog --version
