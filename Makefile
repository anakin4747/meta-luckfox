
CQFD := scripts/cqfd

.PHONY: help # prints this message
help:
	@scripts/list-targets $(MAKEFILE_LIST)

.PHONY: init # builds the cqfd build container
init:
	$(CQFD) init

.PHONY: shell # enters a kas shell
shell: init
	$(CQFD) run kas shell

.PHONY: lint # lints all commit messages with cocogitto
lint: init
	$(CQFD) run 'cog check'

.PHONY: menu # make menuconfig for kernel
menu: init
	$(CQFD) run 'kas shell -c "bitbake -c menuconfig virtual/kernel"'

.PHONY: build # build in CI for review or release
build: lint
	scripts/ci-build

.PHONY: layer-check # runs yocto-check-layer on meta-luckfox layers
layer-check: init
	$(CQFD) run 'kas shell -c "yocto-check-layer \
		../sources/meta-luckfox/meta-luckfox-bsp \
		../sources/meta-luckfox/meta-luckfox-distro"'

.PHONY: lock # updates the kas lock file
lock: init
	$(CQFD) run 'kas lock --update'
