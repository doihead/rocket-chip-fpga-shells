## This file is a general .xdc for the ARTY Rev. B
## To use it in a project:
## - uncomment the lines corresponding to used pins
## - rename the used ports (in each line, after get_ports) according to the top level signal names in the project

## Clock signal

set_property -dict { PACKAGE_PIN F22    IOSTANDARD LVCMOS33 } [get_ports { CLK100MHZ }]; #IO_L12P_T1_MRCC_35 Sch=gclk[100]
create_clock -add -name sys_clk_pin -period 10.00 -waveform {0 5} [get_ports {CLK100MHZ}];
create_clock -add -name JTCK        -period 100   -waveform {0 50} [get_ports {jd_2}];


##LEDs

set_property -dict { PACKAGE_PIN L16    IOSTANDARD LVCMOS33 } [get_ports { led_0 }]; #IO_L24N_T3_35 Sch=led[4]
set_property -dict { PACKAGE_PIN L18    IOSTANDARD LVCMOS33 } [get_ports { led_1 }]; #IO_25_35 Sch=led[5]

##Buttons

set_property -dict { PACKAGE_PIN R13    IOSTANDARD LVCMOS33 } [get_ports { btn_0 }]; #IO_L6N_T0_VREF_16 Sch=btn[0]
set_property -dict { PACKAGE_PIN T18    IOSTANDARD LVCMOS33 } [get_ports { btn_1 }]; #IO_L11P_T1_SRCC_16 Sch=btn[1]


set_property -dict { PACKAGE_PIN L16   IOSTANDARD LVCMOS33  IOB TRUE } [get_ports { qspi_sck }];
create_clock -add -name qspi_sck_pin -period 20.00 -waveform {0 10}    [get_ports { qspi_sck }];
set_property -dict { PACKAGE_PIN L13   IOSTANDARD LVCMOS33  IOB TRUE } [get_ports { qspi_cs }]; #IO_L6P_T0_FCS_B_14 Sch=qspi_cs
set_property -dict { PACKAGE_PIN K17   IOSTANDARD LVCMOS33  IOB TRUE  PULLUP TRUE } [get_ports { qspi_dq_0 }]; #IO_L1P_T0_D00_MOSI_14 Sch=qspi_dq[0]
set_property -dict { PACKAGE_PIN K18   IOSTANDARD LVCMOS33  IOB TRUE  PULLUP TRUE } [get_ports { qspi_dq_1 }]; #IO_L1N_T0_D01_DIN_14 Sch=qspi_dq[1]
set_property -dict { PACKAGE_PIN L14   IOSTANDARD LVCMOS33  IOB TRUE  PULLUP TRUE } [get_ports { qspi_dq_2 }]; #IO_L2P_T0_D02_14 Sch=qspi_dq[2]
set_property -dict { PACKAGE_PIN M14   IOSTANDARD LVCMOS33  IOB TRUE  PULLUP TRUE } [get_ports { qspi_dq_3 }]; #IO_L2N_T0_D03_14 Sch=qspi_dq[3]


set_clock_groups -asynchronous \
  -group [list \
     [get_clocks -include_generated_clocks -of_objects [get_ports jd_2]]] \
  -group [list \
     [get_clocks -of_objects [get_pins ip_mmcm/inst/mmcm_adv_inst/CLKOUT0]]] \
  -group [list \
     [get_clocks -of_objects [get_pins ip_mmcm/inst/mmcm_adv_inst/CLKOUT1]] \
     [get_clocks -of_objects [get_pins ip_mmcm/inst/mmcm_adv_inst/CLKOUT2]]]
