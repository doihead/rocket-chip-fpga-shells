package sifive.fpgashells.shell.xilinx

import chisel3._
import chisel3.experimental.dataview._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.prci._
import org.chipsalliance.cde.config._
import sifive.fpgashells.clocks._
import sifive.fpgashells.devices.xilinx.xilinxnexys4ddrmig._
import sifive.fpgashells.ip.xilinx._
import sifive.fpgashells.shell._

class SysClockNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: ClockInputDesignInput, val shellInput: ClockInputShellInput)
  extends SingleEndedClockInputXilinxPlacedOverlay(name, designInput, shellInput)
{
  val node = shell { ClockSourceNode(freqMHz = 100, jitterPS = 50) }

  shell { InModuleBody {
    val clk: Clock = io
    shell.xdc.addPackagePin(clk, "E3")
    shell.xdc.addIOStandard(clk, "LVCMOS33")
  } }
}
class SysClockNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: ClockInputShellInput)(implicit val valName: ValName)
  extends ClockInputShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: ClockInputDesignInput) = new SysClockNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class SDIONexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: SPIDesignInput, val shellInput: SPIShellInput)
  extends SDIOXilinxPlacedOverlay(name, designInput, shellInput)
{
  shell { InModuleBody {
    val packagePinsWithPackageIOs = Seq(("B1", IOPin(io.spi_clk)),
      ("C1", IOPin(io.spi_cs)),
      ("C2", IOPin(io.spi_dat(0))),
      ("E1", IOPin(io.spi_dat(1))),
      ("F1", IOPin(io.spi_dat(2))),
      ("D2", IOPin(io.spi_dat(3))))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
      shell.xdc.addIOB(io)
    } }
    packagePinsWithPackageIOs drop 1 foreach { case (pin, io) => {
      shell.xdc.addPullup(io)
    } }
  } }
}
class SDIONexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: SPIShellInput)(implicit val valName: ValName)
  extends SPIShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: SPIDesignInput) = new SDIONexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class SPIFlashNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: SPIFlashDesignInput, val shellInput: SPIFlashShellInput)
  extends SPIFlashXilinxPlacedOverlay(name, designInput, shellInput)
{

  shell { InModuleBody {
    val packagePinsWithPackageIOs = Seq(("E9", IOPin(io.qspi_sck)),
      ("L13", IOPin(io.qspi_cs)),
      ("K17", IOPin(io.qspi_dq(0))),
      ("K18", IOPin(io.qspi_dq(1))),
      ("L14", IOPin(io.qspi_dq(2))),
      ("M14", IOPin(io.qspi_dq(3))))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
    } }
    packagePinsWithPackageIOs drop 1 foreach { case (pin, io) => {
      shell.xdc.addPullup(io)
    } }
  } }
}
class SPIFlashNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: SPIFlashShellInput)(implicit val valName: ValName)
  extends SPIFlashShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: SPIFlashDesignInput) = new SPIFlashNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class TracePMODNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: TracePMODDesignInput, val shellInput: TracePMODShellInput)
  extends TracePMODXilinxPlacedOverlay(name, designInput, shellInput, packagePins = Seq("U12", "V12", "V10", "V11", "U14", "V14", "T13", "U13"))
class TracePMODNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: TracePMODShellInput)(implicit val valName: ValName)
  extends TracePMODShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: TracePMODDesignInput) = new TracePMODNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class GPIOPMODNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: GPIOPMODDesignInput, val shellInput: GPIOPMODShellInput)
  extends GPIOPMODXilinxPlacedOverlay(name, designInput, shellInput)
{
  shell { InModuleBody {
    val packagePinsWithPackageIOs = Seq(("D14", IOPin(io.gpio_pmod_0)), //These are PMOD B
      ("F16", IOPin(io.gpio_pmod_1)),
      ("G16", IOPin(io.gpio_pmod_2)),
      ("H14", IOPin(io.gpio_pmod_3)),
      ("E16", IOPin(io.gpio_pmod_4)),
      ("F13", IOPin(io.gpio_pmod_5)),
      ("G13", IOPin(io.gpio_pmod_6)),
      ("H16", IOPin(io.gpio_pmod_7)))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
    } }
    packagePinsWithPackageIOs drop 7 foreach { case (pin, io) => {
      shell.xdc.addPullup(io)
    } }
  } }
}
class GPIOPMODNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: GPIOPMODShellInput)(implicit val valName: ValName)
  extends GPIOPMODShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: GPIOPMODDesignInput) = new GPIOPMODNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class UARTNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: UARTDesignInput, val shellInput: UARTShellInput)
  extends UARTXilinxPlacedOverlay(name, designInput, shellInput, false)
{
  shell { InModuleBody {
    val packagePinsWithPackageIOs = Seq(("C4", IOPin(io.rxd)),
      ("D4", IOPin(io.txd)))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
      shell.xdc.addIOB(io)
    } }
  } }
}
class UARTNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: UARTShellInput)(implicit val valName: ValName)
  extends UARTShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: UARTDesignInput) = new UARTNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//LEDS - r0, g0, b0, r1, g1, b1, 16 normal leds_
object LEDNexys4PinConstraints{
  val pins = Seq("N15", "M16", "R12", "N16", "R11", "G14", "H17", "K15", "J13", "N14", "R18", "V17", "U17", "U16", "V16", "T15", "U14", "T16", "V15", "V14", "V12", "V11")
}
class LEDNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: LEDDesignInput, val shellInput: LEDShellInput)
  extends LEDXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(LEDNexys4PinConstraints.pins(shellInput.number)))
class LEDNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: LEDShellInput)(implicit val valName: ValName)
  extends LEDShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: LEDDesignInput) = new LEDNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//SWs
object SwitchNexys4PinConstraints{
  val pins = Seq("J15", "L16", "M13", "R15", "R17")
}
class SwitchNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: SwitchDesignInput, val shellInput: SwitchShellInput)
  extends SwitchXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(SwitchNexys4PinConstraints.pins(shellInput.number)))
class SwitchNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: SwitchShellInput)(implicit val valName: ValName)
  extends SwitchShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: SwitchDesignInput) = new SwitchNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//Buttons
object ButtonNexys4PinConstraints {
  val pins = Seq("N17", "M18", "P17", "M17")
}
class ButtonNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: ButtonDesignInput, val shellInput: ButtonShellInput)
  extends ButtonXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(ButtonNexys4PinConstraints.pins(shellInput.number)))
class ButtonNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: ButtonShellInput)(implicit val valName: ValName)
  extends ButtonShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: ButtonDesignInput) = new ButtonNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class JTAGDebugBScanNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: JTAGDebugBScanDesignInput, val shellInput: JTAGDebugBScanShellInput)
 extends JTAGDebugBScanXilinxPlacedOverlay(name, designInput, shellInput)
class JTAGDebugBScanNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: JTAGDebugBScanShellInput)(implicit val valName: ValName)
  extends JTAGDebugBScanShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: JTAGDebugBScanDesignInput) = new JTAGDebugBScanNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

// PMOD JD used for JTAG
class JTAGDebugNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: JTAGDebugDesignInput, val shellInput: JTAGDebugShellInput)
  extends JTAGDebugXilinxPlacedOverlay(name, designInput, shellInput)
{
  shell { InModuleBody {
    shell.sdc.addClock("JTCK", IOPin(io.jtag_TCK), 10)
    shell.sdc.addGroup(clocks = Seq("JTCK"))
    shell.xdc.clockDedicatedRouteFalse(IOPin(io.jtag_TCK))
    val packagePinsWithPackageIOs = Seq(("G1", IOPin(io.jtag_TCK)),  //pin JD-3
      ("F3", IOPin(io.jtag_TMS)),  //pin JD-8
      ("G2", IOPin(io.jtag_TDI)),  //pin JD-7
      ("H4", IOPin(io.jtag_TDO)),  //pin JD-1
      ("G2", IOPin(io.srst_n)))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
      shell.xdc.addPullup(io)
    } }
  } }
}
class JTAGDebugNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: JTAGDebugShellInput)(implicit val valName: ValName)
  extends JTAGDebugShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: JTAGDebugDesignInput) = new JTAGDebugNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//cjtag
class cJTAGDebugNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: cJTAGDebugDesignInput, val shellInput: cJTAGDebugShellInput)
  extends cJTAGDebugXilinxPlacedOverlay(name, designInput, shellInput)
{
  shell { InModuleBody {
    shell.sdc.addClock("JTCKC", IOPin(io.cjtag_TCKC), 10)
    shell.sdc.addGroup(clocks = Seq("JTCKC"))
    shell.xdc.clockDedicatedRouteFalse(IOPin(io.cjtag_TCKC))
    val packagePinsWithPackageIOs = Seq(("G1", IOPin(io.cjtag_TCKC)),  //pin JD-3
      ("F3", IOPin(io.cjtag_TMSC)),  //pin JD-8
      ("G2", IOPin(io.srst_n)))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
    } }
      shell.xdc.addPullup(IOPin(io.cjtag_TCKC))
      shell.xdc.addPullup(IOPin(io.srst_n))
  } }
}
class cJTAGDebugNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: cJTAGDebugShellInput)(implicit val valName: ValName)
  extends cJTAGDebugShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: cJTAGDebugDesignInput) = new cJTAGDebugNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

case object Nexys4DDRSize extends Field[BigInt](0x8000000L * 1) // 128 MB
class DDRNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: DDRDesignInput, val shellInput: DDRShellInput)
  extends DDRPlacedOverlay[XilinxNexys4DDRMIGPads](name, designInput, shellInput)
{
  val size = p(Nexys4DDRSize)

  val ddrClk1 = shell { ClockSinkNode(freqMHz = 100)}
  val ddrClk2 = shell { ClockSinkNode(freqMHz = 200)}
  val ddrGroup = shell { ClockGroup() }
  ddrClk1 := di.wrangler := ddrGroup := di.corePLL
  ddrClk2 := di.wrangler := ddrGroup
  
  val migParams = XilinxNexys4DDRMIGParams(address = AddressSet.misaligned(di.baseAddress, size))
  val mig = LazyModule(new XilinxNexys4DDRMIG(migParams))
  val ddrUI     = shell { ClockSourceNode(freqMHz = 100) }
  val areset    = shell { ClockSinkNode(Seq(ClockSinkParameters())) }
  areset := di.wrangler := ddrUI

  def overlayOutput = DDROverlayOutput(ddr = mig.node)
  def ioFactory = new XilinxNexys4DDRMIGPads(size)

  shell { InModuleBody {
    require (shell.sys_clock.get.isDefined, "Use of DDRNexys4PlacedOverlay depends on SysClockNexys4PlacedOverlay")
    val (sys, _) = shell.sys_clock.get.get.overlayOutput.node.out(0)
    val (ui, _) = ddrUI.out(0)
    val (dclk1, _) = ddrClk1.in(0)
    val (dclk2, _) = ddrClk2.in(0)
    val (ar, _) = areset.in(0)
    val port = mig.module.io.port
    
    io <> port.viewAsSupertype(new XilinxNexys4DDRMIGPads(mig.depth))
    ui.clock := port.ui_clk
    ui.reset := !port.mmcm_locked || port.ui_clk_sync_rst
    port.sys_clk_i := dclk1.clock.asUInt
    port.clk_ref_i := dclk2.clock.asUInt
    port.sys_rst := shell.pllReset
    port.aresetn := !(ar.reset.asBool)
  } }

  shell.sdc.addGroup(clocks = Seq("clk_pll_i"), pins = Seq(mig.island.module.blackbox.io.ui_clk))
}
class DDRNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: DDRShellInput)(implicit val valName: ValName)
  extends DDRShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: DDRDesignInput) = new DDRNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//Core to shell external resets
class CTSResetNexys4PlacedOverlay(val shell: Nexys4DDRShellBasicOverlays, name: String, val designInput: CTSResetDesignInput, val shellInput: CTSResetShellInput)
  extends CTSResetPlacedOverlay(name, designInput, shellInput)
class CTSResetNexys4ShellPlacer(val shell: Nexys4DDRShellBasicOverlays, val shellInput: CTSResetShellInput)(implicit val valName: ValName)
  extends CTSResetShellPlacer[Nexys4DDRShellBasicOverlays] {
  def place(designInput: CTSResetDesignInput) = new CTSResetNexys4PlacedOverlay(shell, valName.name, designInput, shellInput)
}


abstract class Nexys4DDRShellBasicOverlays()(implicit p: Parameters) extends Series7Shell {
  // Order matters; ddr depends on sys_clock
  val sys_clock = Overlay(ClockInputOverlayKey, new SysClockNexys4ShellPlacer(this, ClockInputShellInput()))
  val led       = Seq.tabulate(16)(i => Overlay(LEDOverlayKey, new LEDNexys4ShellPlacer(this, LEDMetas(i))(valName = ValName(s"led_$i"))))
  val switch    = Seq.tabulate(4)(i => Overlay(SwitchOverlayKey, new SwitchNexys4ShellPlacer(this, SwitchShellInput(number = i))(valName = ValName(s"switch_$i"))))
  val button    = Seq.tabulate(4)(i => Overlay(ButtonOverlayKey, new ButtonNexys4ShellPlacer(this, ButtonShellInput(number = i))(valName = ValName(s"button_$i"))))
  val ddr       = Overlay(DDROverlayKey, new DDRNexys4ShellPlacer(this, DDRShellInput()))
  val uart      = Overlay(UARTOverlayKey, new UARTNexys4ShellPlacer(this, UARTShellInput()))
  val sdio      = Overlay(SPIOverlayKey, new SDIONexys4ShellPlacer(this, SPIShellInput()))
  val jtag      = Overlay(JTAGDebugOverlayKey, new JTAGDebugNexys4ShellPlacer(this, JTAGDebugShellInput()))
  val cjtag     = Overlay(cJTAGDebugOverlayKey, new cJTAGDebugNexys4ShellPlacer(this, cJTAGDebugShellInput()))
  val spi_flash = Overlay(SPIFlashOverlayKey, new SPIFlashNexys4ShellPlacer(this, SPIFlashShellInput()))
  val cts_reset = Overlay(CTSResetOverlayKey, new CTSResetNexys4ShellPlacer(this, CTSResetShellInput()))
  val jtagBScan = Overlay(JTAGDebugBScanOverlayKey, new JTAGDebugBScanNexys4ShellPlacer(this, JTAGDebugBScanShellInput()))

  def LEDMetas(i: Int): LEDShellInput =
    LEDShellInput(
      color = if((i < 12) && (i % 3 == 1)) "green" else if((i < 12) && (i % 3 == 2)) "blue" else "red",
      rgb = (i < 12),
      number = i)
}

class Nexys4DDRShell()(implicit p: Parameters) extends Nexys4DDRShellBasicOverlays
{
  val resetPin = InModuleBody { Wire(Bool()) }
  // PLL reset causes
  val pllReset = InModuleBody { Wire(Bool()) }

  val topDesign = LazyModule(p(DesignKey)(designParameters))

  // Place the sys_clock at the Shell if the user didn't ask for it
  p(ClockInputOverlayKey).foreach(_.place(ClockInputDesignInput()))
  override lazy val module = new Impl
  class Impl extends LazyRawModuleImp(this) {
    override def provideImplicitClockToLazyChildren = true

    val reset = IO(Input(Bool()))
    xdc.addBoardPin(reset, "reset")

    val reset_ibuf = Module(new IBUF)
    reset_ibuf.io.I := reset
    val sysclk: Clock = sys_clock.get() match {
      case Some(x: SysClockNexys4PlacedOverlay) => x.clock
    }
    val powerOnReset = PowerOnResetFPGAOnly(sysclk)
    sdc.addAsyncPath(Seq(powerOnReset))

    resetPin := reset_ibuf.io.O

    pllReset :=
      (!reset_ibuf.io.O) || powerOnReset //Nexys4DDR is active low reset
  }
}

class Nexys4DDRShellGPIOPMOD()(implicit p: Parameters) extends Nexys4DDRShellBasicOverlays
//This is the Shell used for coreip Nexys4 builds, with GPIOS and trace signals on the pmods
{
  // PLL reset causes
  val pllReset = InModuleBody { Wire(Bool()) }

  val gpio_pmod = Overlay(GPIOPMODOverlayKey, new GPIOPMODNexys4ShellPlacer(this, GPIOPMODShellInput()))
  val trace_pmod = Overlay(TracePMODOverlayKey, new TracePMODNexys4ShellPlacer(this, TracePMODShellInput()))

  val topDesign = LazyModule(p(DesignKey)(designParameters))

  // Place the sys_clock at the Shell if the user didn't ask for it
  p(ClockInputOverlayKey).foreach(_.place(ClockInputDesignInput()))

  override lazy val module = new LazyRawModuleImp(this) {
    override def provideImplicitClockToLazyChildren = true
    val reset = IO(Input(Bool()))
    xdc.addBoardPin(reset, "reset")

    val reset_ibuf = Module(new IBUF)
    reset_ibuf.io.I := reset

    val sysclk: Clock = sys_clock.get() match {
      case Some(x: SysClockNexys4PlacedOverlay) => x.clock
    }
    val powerOnReset = PowerOnResetFPGAOnly(sysclk)
    sdc.addAsyncPath(Seq(powerOnReset))
    val ctsReset: Bool = cts_reset.get() match {
      case Some(x: CTSResetNexys4PlacedOverlay) => x.designInput.rst
      case None => false.B
    }

    pllReset :=
      (!reset_ibuf.io.O) || powerOnReset || ctsReset //Nexys4DDR is active low reset
  }
}

/*
   Copyright 2016 SiFive, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
