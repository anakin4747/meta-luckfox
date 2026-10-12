
.PHONY: help # prints this message
help:
	@scripts/list-targets $(MAKEFILE_LIST)

.PHONY: layer-check # runs yocto-check-layer on meta-luckfox layers
layer-check:
	kas shell -c "yocto-check-layer \
		../sources/meta-luckfox/meta-luckfox-bsp \
		../sources/meta-luckfox/meta-luckfox-distro"

