SHELL_PREFIX :=
ifndef IN_NIX_SHELL
SHELL_PREFIX := nix develop --experimental-features 'nix-command flakes' --command
endif

.PHONY: make
make:
	$(SHELL_PREFIX) make layer-check

.PHONY: layer-check # runs yocto-check-layer on meta-luckfox layers
layer-check:
	kas shell -c "yocto-check-layer \
		../sources/meta-luckfox/meta-luckfox-bsp \
		../sources/meta-luckfox/meta-luckfox-distro"

.PHONY: help # prints this message
help:
	@scripts/list-targets $(MAKEFILE_LIST)











some peoples security policy be like:

	touch SECURITY.md

