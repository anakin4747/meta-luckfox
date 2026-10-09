
# meta-luckfox

Kas-based yocto build for Luckfox Pico Ultra W

References:
- https://github.com/LuckfoxTECH/Luckfox-Pico-docs
- https://wiki.luckfox.com/Luckfox-Pico-RV1106/Getting-Started
- sources/yocto-docs/

## Layers

### `meta-luckfox-bsp`

`meta-luckfox-bsp` is the BSP layer

Always follow `sources/yocto-docs/documentation/bsp-guide/bsp.rst` when working
on the BSP layer

#### Machines

- `luckfox-pico-ultra-w`: Luckfox Pico Ultra W (Rockchip RV1106G3, Cortex-A7,
  8GB eMMC, 10/100 Ethernet with PoE, USB OTG + host, SDIO
  Wi-Fi/BT). Shared SoC settings live in `conf/machine/include/rv1106.inc`

The devicetree shall be source controlled with the machine layer
The defconfig shall be source controlled with the machine layer

### `meta-luckfox-distro`

`meta-luckfox-distro` is the example distro layer for this board

Always follow
`sources/yocto-docs/documentation/dev-manual/custom-distribution.rst`
when working on the distro layer

#### Images

Always follow
`sources/yocto-docs/documentation/dev-manual/customizing-images.rst` section
`Customizing Images Using Custom .bb Files` when working on the custom image

## Updating Dependencies

```sh
nix develop
kas lock --update
```

