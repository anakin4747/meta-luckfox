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

RUN pip3 install --break-system-packages kas
