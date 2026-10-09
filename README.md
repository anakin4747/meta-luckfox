
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

## Hardware

Summary of `docs/schematic.pdf` (Luckfox Pico Ultra W; the "Ultra" variant is
the same board without Wi-Fi).

### SoC

- Rockchip RV1106G3 (U2), single core ARM Cortex-A7, DDR on package
- Clocks: 24MHz main crystal (Y1), 32.768kHz RTC crystal (Y2)
- Rails: `VDD_ARM`, `VDD_0V9` (logic/NPU/VEPU), `VCC_DDR` (1.8V),
  `VCC_1V8`, `VCC_3V3`, `VCC3V3_RTC`, from a 3-channel DCDC (U1) fed by
  `VCC5V0_SYS`

### IO voltage domains

| Domain | Voltage | Functions                                          |
|--------|---------|----------------------------------------------------|
| PMUIO  | 3.3V    | GPIO0: UART/PWM/I2C                                |
| VCCIO1 | 3.3V    | GPIO1_A/B: UART/I2C/PWM/JTAG                       |
| VCCIO2 | 1.8V    | SARADC_IN0/IN1 (GPIO4_C0/C1)                       |
| VCCIO3 | flash   | eMMC/FSPI (GPIO4_A/B)                              |
| VCCIO4 | 3.3V    | SDMMC (GPIO3_A), used for the Wi-Fi module         |
| VCCIO5 | 3.3V    | GPIO2_A/B: SDIO/I2S/LCDC/UART/PWM/I2C              |
| VCCIO6 | 3.3V    | GPIO1_C/D: LCDC/CIF/SPI/UART/I2C/PWM               |
| VCCIO7 | 1.8V    | MIPI CSI/LVDS RX (GPI3_B/C, input only), GPIO3_C/D |

### Storage

- 8GB eMMC (U7), 8-bit bus on EMMC_D0..D7/CMD/CLK, `FLASH_ALE/EMMC_RST`
- No SD card slot (SDMMC is wired to the Wi-Fi module)

### Wireless (W variant only)

- SKI.WB800DCS Wi-Fi/BT module (U6) with on-board antenna
- Wi-Fi over SDIO on SDMMC (`SD_D0..D3`, `SD_CMD`, `SD_CLK`)
- BT over UART1_M0 (GPIO1_A3 TX / GPIO1_A4 RX, GPIO0_A5 RTS / GPIO0_A6 CTS)
- `WL_EN`: GPIO1_A2, `HOST_WAKE_BT`: GPIO0_A2
- `WL_HOST_WAKE` and `BT_WAKE_HOST` go through level shifters (U9) to
  GPI3_B0/GPI3_B1

### Ethernet

- 10/100 PHY built into the SoC (FEPHY), RJ45 jack with magnetics (RJ1)
- PoE: center taps go to the PoE header (P4), PoE input on POUT1

### USB

- One USB 2.0 controller (`USB_DP`/`USB_DM`), shared through a USB switch (U10)
  - USB Type-C (H5), with 5.1K pull-downs on CC1/CC2 (device/OTG)
  - USB Type-A (J2), host
- VBUS detection on `USB_VBUSDET` (`USB_DET_IN`)

### Audio

- Audio codec built into the SoC
- MIC0: on-board MEMS microphone (MIC1)
- MIC1 and `MICBIAS` routed to the GPIO header
- `LINEOUT` drives an amplifier (U3) to the speaker connector (H4, MX1.25 2P)

### Camera

- MIPI CSI-2 2-lane, 20-pin 0.5mm FPC (H1)
- `MIPI_I2C` (I2C4_M2: GPIO3_C7 SCL / GPIO3_D0 SDA), `MIPI_CLK0_OUT` as MCLK,
  reset on GPIO3_C6

### Display

- RGB666 parallel LCD, 40-pin 0.5mm FPC (H2)
  - `RGB_DB0..17` on GPIO1_C*/GPIO1_D*/GPIO2_A*/GPIO2_B*
  - `RGB_CLK`, `RGB_DE`, `RGB_HSYNC`, `RGB_VSYNC` on GPIO1_D0..D3
  - SPI init/control lines: GPIO0_A3, GPIO0_A4
- Backlight boost driver (U4), enabled by GPIO3_D3, ~40mA
- Touch on the same FPC: `TP_SDA`/`TP_SCL` through level shifters (U8) on
  GPIO3_D2/GPIO3_D1

### Debug and control

- Debug UART: UART2_M1 (GPIO1_B2 TX / GPIO1_B3 RX), on the GPIO header
- RECOVERY key on SARADC_IN0 (GPIO4_C0, must always be pulled up)
- Reset key (nPOR)
- User LED (LED1)
- 2-pin SH1.0 header (H3)

### GPIO headers

P1 (2x13, left):

| Pin | Signal    | Pin | Signal    |
|-----|-----------|-----|-----------|
| 1   | VCC_3V3   | 2   | 5V        |
| 3   | GPIO1_A0  | 4   | 5V        |
| 5   | GPIO1_A1  | 6   | GND       |
| 7   | GPIO1_B0  | 8   | GPIO1_B1  |
| 9   | GND       | 10  | GPIO1_D0  |
| 11  | GPIO1_D1  | 12  | VCC_3V3   |
| 13  | GPIO1_C2  | 14  | GPIO1_C3  |
| 15  | GPIO1_C1  | 16  | GND       |
| 17  | GPIO1_B2  | 18  | GPIO1_B3  |
| 19  | GPIO1_C6  | 20  | GND       |
| 21  | GPIO2_A7  | 22  | GPIO2_A6  |
| 23  | GND       | 24  | GPIO1_D3  |
| 25  | GPIO1_C0  | 26  | GPIO1_D2  |

P2 (2x13, right):

| Pin | Signal         | Pin | Signal    |
|-----|----------------|-----|-----------|
| 1   | GND            | 2   | GPIO4_C0  |
| 3   | GPIO4_C1       | 4   | GND       |
| 5   | CODEC_MICBIAS  | 6   | CODEC_MIC1P |
| 7   | CODEC_MIC1N    | 8   | GND       |
| 9   | GPIO1_C7       | 10  | GPIO2_B0  |
| 11  | GPIO2_B1       | 12  | GND       |
| 13  | GND            | 14  | VCC_3V3   |
| 15  | GND            | 16  | GPIO1_C4  |
| 17  | GPIO1_C5       | 18  | GPIO2_A1  |
| 19  | GPIO2_A0       | 20  | GPIO2_A5  |
| 21  | GPIO2_A4       | 22  | GPIO2_A2  |
| 23  | GPIO2_A3       | 24  | GND       |
| 25  | 1V8            | 26  | GND       |

The header pin numbering is read from the net order in the schematic; check it
against the official pinout before relying on it.

### Parts needing Linux drivers

Part numbers as printed in `docs/schematic.pdf`. "Not given" means the
schematic only has a generic label.

| Ref | Function                   | Part number             | Notes                                                       |
|-----|----------------------------|-------------------------|-------------------------------------------------------------|
| U2  | SoC                        | Rockchip RV1106G3       | Built-in eMMC/SDMMC, FEPHY, USB 2.0, codec, RTC, SARADC, MIPI CSI, RGB LCDC, I2C/SPI/UART/PWM |
| U6  | Wi-Fi/BT module            | SKI.WB800DCS.2          | Wi-Fi/BT chipset inside the module not given                |
| U7  | eMMC                       | Not given ("EMMC-8G")   | Standard eMMC, handled by the SoC MMC driver                |
| U10 | USB 2.0 switch (OTG/host)  | Not given ("USB SWITCH")| Selected by `SEL`/`OE` pins; controlling GPIO not readable  |
| U3  | Audio amplifier            | Not given               | `SHUTDOWN` pin may need a GPIO / simple-amplifier node      |
| U4  | Backlight boost driver     | Not given               | `EN` on GPIO3_D3                                            |
| U1  | 3-channel DCDC             | Not given ("DCDC")      | Fixed outputs, no bus: fixed regulators in the device tree  |
| U5  | LDO                        | Not given ("LDO")       | Fixed output, no bus                                        |
| MIC1| Microphone                 | Not given               | Analog, into the SoC codec MIC0                             |

Not on the board (connector only, the part depends on what you plug in):
camera sensor (H1), LCD panel (H2), touch controller (H2).

No driver needed: level shifter MOSFETs NDC7002N (U8, U9), ESD diodes
ESD5451N, TVS MF5.0CA, Schottky diodes MBR0530 / B5819WS / MBR230LSFT1G.

## Roadmap: `linux-yocto` support

Goal: boot the Luckfox Pico Ultra W on an upstream-based `linux-yocto` kernel
instead of the Rockchip/Luckfox vendor kernel (5.10.160 in the Luckfox SDK,
`develop-5.10`/`6.1`/`6.6` in `rockchip-linux/kernel`).

Status is based on a source search of mainline (7.3-rc6, Oct 2026) and has not
been verified by building or booting. Re-check each item before starting work;
patches may already be on <https://lore.kernel.org/linux-rockchip/>.

The vendor kernel (`sysdrv/source/kernel` in
<https://github.com/LuckfoxTECH/luckfox-pico>) is the reference for register
layouts and quirks when porting.

### Already upstream

- [x] Clock controller: `drivers/clk/rockchip/clk-rv1106.c` (`rockchip,rv1106-cru`)
  - [ ] Check which release it landed in; backport to the `linux-yocto` version
        in use (6.18) if missing
- [x] Pin controller: `rockchip,rv1106-pinctrl` in `pinctrl-rockchip.c`

### Phase 1: boot to a serial console

- [ ] `rv1106.dtsi` SoC device tree: CPU, GIC, arch timer, CRU, GRF, GPIO,
      pinctrl, UARTs (vendor `rv1106.dtsi` and mainline `rv1103b.dtsi` as
      references)
- [ ] `rv1106g-luckfox-pico-ultra-w.dts` board device tree, plus
      `Documentation/devicetree/bindings/arm/rockchip.yaml` entry
- [ ] UART: expected to work with `snps,dw-apb-uart`, verify on UART2_M1
- [ ] Fixed regulators for the DCDC (U1) and LDO (U5) rails
- [ ] Bootloader able to load the mainline kernel (vendor U-Boot or mainline
      U-Boot RV1106 support)

### Phase 2: storage

- [ ] eMMC: `dw_mmc-rockchip` with `rockchip,rv1106-dw-mshc` compatible
      (or generic fallback), check clock phase/tuning
- [ ] SDIO on SDMMC for the Wi-Fi module (same controller driver)
- [ ] SARADC: add RV1106 to `rockchip_saradc.c`; needed for the RECOVERY key
      (`adc-keys`)

### Phase 3: networking and USB

- [ ] Ethernet MAC: add RV1106 ops to `dwmac-rk.c`
- [ ] Integrated 10/100 PHY (FEPHY): add RV1106 support to
      `drivers/net/phy/rockchip.c` or a new PHY driver
- [ ] USB 2.0 PHY: add RV1106 to `phy-rockchip-inno-usb2.c`
- [ ] USB controller: verify generic DWC driver binding works for OTG and host
- [ ] USB switch (U10): GPIO controlled (`SEL`/`OE`), model as a
      `gpio-sbu-mux`/usb role switch or a fixed GPIO hog; controlling GPIO
      still to be identified on the schematic

### Phase 4: board peripherals

- [ ] RTC: driver for the on-SoC RTC (no mainline driver found)
- [ ] PWM: add RV1106 to `pwm-rockchip.c`
- [ ] I2C: verify `rk3x-i2c` with an RV1106 compatible
- [ ] SPI: verify `spi-rockchip` with an RV1106 compatible
- [ ] Watchdog, OTP/eFuse (`nvmem`), thermal sensor (TSADC): verify/port
- [ ] User LED (LED1): `gpio-leds`

### Phase 5: wireless

- [ ] AIC8800DC Wi-Fi over SDIO: no mainline driver; either upstream a driver
      or package the out-of-tree driver (`sysdrv/drv_ko/wifi/aic8800dc` in the
      Luckfox SDK) as a Yocto module recipe
- [ ] AIC8800DC Bluetooth over UART1_M0: check `hci_uart`/`btaic` support;
      likely needs firmware loading support
- [ ] Firmware files: `linux-firmware` or a separate recipe

### Phase 6: audio

- [ ] On-SoC audio codec (acodec): new ASoC codec driver (vendor
      `rv1106_codec` as reference)
- [ ] I2S/DAI driver binding for the codec path
- [ ] Line out amplifier (U3): `simple-audio-amplifier` with the `SHUTDOWN` GPIO
- [ ] Sound card: `simple-audio-card` node in the board device tree

### Phase 7: display

- [ ] VOP (LCDC): add RV1106 to `rockchip_vop_reg.c` / rockchipdrm
- [ ] RGB666 parallel output to the 40-pin FPC
- [ ] Backlight (U4): `gpio-backlight` or `pwm-backlight` on GPIO3_D3
- [ ] Panel and touch controller: depend on the attached display, not on the
      board

### Phase 8: camera and NPU (largest effort, may stay vendor only)

- [ ] MIPI CSI-2 receiver D-PHY
- [ ] VICAP/CIF: extend `rkcif` for RV1106
- [ ] ISP: no mainline support for this ISP generation (vendor `rkisp`)
- [ ] Camera sensor: depends on the attached module
- [ ] NPU: no mainline driver (vendor RKNPU); possibly via the `accel`
      subsystem in the future
- [ ] Video encoder (VEPU): `hantro`/`rkvenc` support for RV1106

### Yocto integration (in parallel)

- [ ] `linux-yocto` bbappend in `meta-luckfox-bsp/recipes-kernel/linux` with
      the board device tree and backported patches
- [ ] Kernel config fragment (`.cfg`) enabling the RV1106 drivers
- [ ] Set `PREFERRED_PROVIDER_virtual/kernel` and `KERNEL_DEVICETREE` in
      `luckfox-pico-ultra-w.conf` (current value is a placeholder)
- [ ] Bootloader recipe and WIC layout matching `sd_update.txt` offsets
