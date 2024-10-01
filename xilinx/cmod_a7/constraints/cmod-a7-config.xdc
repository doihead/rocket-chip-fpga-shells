set_property -dict [list \
	CONFIG_VOLTAGE {3.3} \
	CFGBVS {VCCO} \
	BITSTREAM.CONFIG.SPI_BUSWIDTH {1} \
	] [current_design]
