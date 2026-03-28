set_property -dict [list \
	CONFIG_VOLTAGE {3.3} \
	CFGBVS {VCCO} \
	BITSTREAM.CONFIG.SPI_BUSWIDTH {4} \
	PARAM.FREQUENCY 30000000
	] [current_design]
