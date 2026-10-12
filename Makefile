
CQFD := scripts/cqfd

.PHONY: help # prints this message
help:
	@scripts/list-targets $(MAKEFILE_LIST)

.PHONY: init # builds the cqfd build container
init:
	$(CQFD) init

.PHONY: layer-check # runs yocto-check-layer on meta-luckfox layers
layer-check: init
	$(CQFD) run 'kas shell -c "yocto-check-layer \
		../sources/meta-luckfox/meta-luckfox-bsp \
		../sources/meta-luckfox/meta-luckfox-distro"'

.PHONY: lock # updates the kas lock file
lock: init
	$(CQFD) run 'kas lock --update'
