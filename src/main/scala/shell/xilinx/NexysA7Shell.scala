package sifive.fpgashells.shell.xilinx

import chisel3._
import chisel3.experimental.dataview._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.prci._
import org.chipsalliance.cde.config._
import sifive.fpgashells.clocks._
import sifive.fpgashells.devices.xilinx.xilinxnexysa7mig._
import sifive.fpgashells.ip.xilinx._
import sifive.fpgashells.shell._

class SysClockNexysA7PlacedOverlay(val shell: NexysA7ShellBasicOverlays, name: String, val designInput: ClockInputDesignInput, val shellInput: ClockInputShellInput)
  extends SingleEndedClockInputXilinxPlacedOverlay(name, designInput, shellInput)
{
  val node = shell { ClockSourceNode(freqMHz = 100, jitterPS = 50) }

  shell { InModuleBody {
    val clk: Clock = io
    shell.xdc.addPackagePin(clk, "E3")
    shell.xdc.addIOStandard(clk, "LVCMOS33")
  } }
}
class SysClockNexysA7ShellPlacer(val shell: NexysA7ShellBasicOverlays, val shellInput: ClockInputShellInput)(implicit val valName: ValName)
  extends ClockInputShellPlacer[NexysA7ShellBasicOverlays] {
  def place(designInput: ClockInputDesignInput) = new SysClockNexysA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class SDIONexysA7PlacedOverlay(val shell: NexysA7ShellBasicOverlays, name: String, val designInput: SPIDesignInput, val shellInput: SPIShellInput)
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
class SDIONexysA7ShellPlacer(val shell: NexysA7ShellBasicOverlays, val shellInput: SPIShellInput)(implicit val valName: ValName)
  extends SPIShellPlacer[NexysA7ShellBasicOverlays] {
  def place(designInput: SPIDesignInput) = new SDIONexysA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class SPIFlashNexysA7PlacedOverlay(val shell: NexysA7ShellBasicOverlays, name: String, val designInput: SPIFlashDesignInput, val shellInput: SPIFlashShellInput)
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

class SPIFlashNexysA7ShellPlacer(val shell: NexysA7ShellBasicOverlays, val shellInput: SPIFlashShellInput)(implicit val valName: ValName)
  extends SPIFlashShellPlacer[NexysA7ShellBasicOverlays] {
  def place(designInput: SPIFlashDesignInput) = new SPIFlashNexysA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class UARTNexysA7PlacedOverlay(val shell: NexysA7ShellBasicOverlays, name: String, val designInput: UARTDesignInput, val shellInput: UARTShellInput)
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
class UARTNexysA7ShellPlacer(val shell: NexysA7ShellBasicOverlays, val shellInput: UARTShellInput)(implicit val valName: ValName)
  extends UARTShellPlacer[NexysA7ShellBasicOverlays] {
  def place(designInput: UARTDesignInput) = new UARTNexysA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//LEDS - r0, g0, b0, r1, g1, b1, 16 normal leds_
object LEDNexysA7PinConstraints{
  val pins = Seq("N15", "M16", "R12", "N16", "R11", "G14", "H17", "K15", "J13", "N14", "R18", "V17", "U17", "U16", "V16", "T15", "U14", "T16", "V15", "V14", "V12", "V11")
}
class LEDNexysA7PlacedOverlay(val shell: NexysA7ShellBasicOverlays, name: String, val designInput: LEDDesignInput, val shellInput: LEDShellInput)
  extends LEDXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(LEDNexysA7PinConstraints.pins(shellInput.number)))
class LEDNexysA7ShellPlacer(val shell: NexysA7ShellBasicOverlays, val shellInput: LEDShellInput)(implicit val valName: ValName)
  extends LEDShellPlacer[NexysA7ShellBasicOverlays] {
  def place(designInput: LEDDesignInput) = new LEDNexysA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//SWs
object SwitchNexysA7PinConstraints{
  val pins = Seq("J15", "L16", "M13", "R15", "R17")
}
class SwitchNexysA7PlacedOverlay(val shell: NexysA7ShellBasicOverlays, name: String, val designInput: SwitchDesignInput, val shellInput: SwitchShellInput)
  extends SwitchXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(SwitchNexysA7PinConstraints.pins(shellInput.number)))
class SwitchNexysA7ShellPlacer(val shell: NexysA7ShellBasicOverlays, val shellInput: SwitchShellInput)(implicit val valName: ValName)
  extends SwitchShellPlacer[NexysA7ShellBasicOverlays] {
  def place(designInput: SwitchDesignInput) = new SwitchNexysA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//Buttons
object ButtonNexysA7PinConstraints {
  val pins = Seq("N17", "M18", "P17", "M17")
}
class ButtonNexysA7PlacedOverlay(val shell: NexysA7ShellBasicOverlays, name: String, val designInput: ButtonDesignInput, val shellInput: ButtonShellInput)
  extends ButtonXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(ButtonNexysA7PinConstraints.pins(shellInput.number)))
class ButtonNexysA7ShellPlacer(val shell: NexysA7ShellBasicOverlays, val shellInput: ButtonShellInput)(implicit val valName: ValName)
  extends ButtonShellPlacer[NexysA7ShellBasicOverlays] {
  def place(designInput: ButtonDesignInput) = new ButtonNexysA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

case object NexysA7Size extends Field[BigInt](0x8000000L) // 128 MB
class DDRNexysA7PlacedOverlay(val shell: NexysA7ShellBasicOverlays, name: String, val designInput: DDRDesignInput, val shellInput: DDRShellInput)
  extends DDRPlacedOverlay[XilinxNexysA7MIGPads](name, designInput, shellInput)
{
  val size = p(NexysA7Size)

  val ddrClk1 = shell { ClockSinkNode(freqMHz = 100)}
  val ddrClk2 = shell { ClockSinkNode(freqMHz = 200)}
  val ddrGroup = shell { ClockGroup() }
  ddrClk1 := di.wrangler := ddrGroup := di.corePLL
  ddrClk2 := di.wrangler := ddrGroup
  
  val migParams = XilinxNexysA7MIGParams(address = AddressSet.misaligned(di.baseAddress, size))
  val mig = LazyModule(new XilinxNexysA7MIG(migParams))
  val ddrUI     = shell { ClockSourceNode(freqMHz = 100) }
  val areset    = shell { ClockSinkNode(Seq(ClockSinkParameters())) }
  areset := di.wrangler := ddrUI

  def overlayOutput = DDROverlayOutput(ddr = mig.node)
  def ioFactory = new XilinxNexysA7MIGPads(size)

  shell { InModuleBody {
    require (shell.sys_clock.get.isDefined, "Use of DDRNexysA7PlacedOverlay depends on SysClockNexysA7PlacedOverlay")
    val (sys, _) = shell.sys_clock.get.get.overlayOutput.node.out(0)
    val (ui, _) = ddrUI.out(0)
    val (dclk1, _) = ddrClk1.in(0)
    val (dclk2, _) = ddrClk2.in(0)
    val (ar, _) = areset.in(0)
    val port = mig.module.io.port
    
    io <> port.viewAsSupertype(new XilinxNexysA7MIGPads(mig.depth))
    ui.clock := port.ui_clk
    ui.reset := !port.mmcm_locked || port.ui_clk_sync_rst
    port.sys_clk_i := dclk1.clock.asUInt
    port.clk_ref_i := dclk2.clock.asUInt
    port.sys_rst := shell.pllReset
    port.aresetn := !(ar.reset.asBool)
  } }

  shell.sdc.addGroup(clocks = Seq("clk_pll_i"), pins = Seq(mig.island.module.blackbox.io.ui_clk))
}
class DDRNexysA7ShellPlacer(val shell: NexysA7ShellBasicOverlays, val shellInput: DDRShellInput)(implicit val valName: ValName)
  extends DDRShellPlacer[NexysA7ShellBasicOverlays] {
  def place(designInput: DDRDesignInput) = new DDRNexysA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//Core to shell external resets
class CTSResetNexysA7PlacedOverlay(val shell: NexysA7ShellBasicOverlays, name: String, val designInput: CTSResetDesignInput, val shellInput: CTSResetShellInput)
  extends CTSResetPlacedOverlay(name, designInput, shellInput)
class CTSResetNexysA7ShellPlacer(val shell: NexysA7ShellBasicOverlays, val shellInput: CTSResetShellInput)(implicit val valName: ValName)
  extends CTSResetShellPlacer[NexysA7ShellBasicOverlays] {
  def place(designInput: CTSResetDesignInput) = new CTSResetNexysA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}


abstract class NexysA7ShellBasicOverlays()(implicit p: Parameters) extends Series7Shell {
  // Order matters; ddr depends on sys_clock
  val sys_clock = Overlay(ClockInputOverlayKey, new SysClockNexysA7ShellPlacer(this, ClockInputShellInput()))
  val led       = Seq.tabulate(22)(i => Overlay(LEDOverlayKey, new LEDNexysA7ShellPlacer(this, LEDMetas(i))(valName = ValName(s"led_$i"))))
  val switch    = Seq.tabulate(4)(i => Overlay(SwitchOverlayKey, new SwitchNexysA7ShellPlacer(this, SwitchShellInput(number = i))(valName = ValName(s"switch_$i"))))
  val button    = Seq.tabulate(4)(i => Overlay(ButtonOverlayKey, new ButtonNexysA7ShellPlacer(this, ButtonShellInput(number = i))(valName = ValName(s"button_$i"))))
  val ddr       = Overlay(DDROverlayKey, new DDRNexysA7ShellPlacer(this, DDRShellInput()))
  val uart      = Overlay(UARTOverlayKey, new UARTNexysA7ShellPlacer(this, UARTShellInput()))
  val sdio      = Overlay(SPIOverlayKey, new SDIONexysA7ShellPlacer(this, SPIShellInput()))
  val spi_flash = Overlay(SPIFlashOverlayKey, new SPIFlashNexysA7ShellPlacer(this, SPIFlashShellInput()))
  val cts_reset = Overlay(CTSResetOverlayKey, new CTSResetNexysA7ShellPlacer(this, CTSResetShellInput()))

  def LEDMetas(i: Int): LEDShellInput =
    LEDShellInput(
      color = if((i < 6) && (i % 3 == 1)) "green" else if((i < 6) && (i % 3 == 2)) "blue" else "red",
      rgb = (i < 6),
      number = i)
}

class NexysA7Shell()(implicit p: Parameters) extends NexysA7ShellBasicOverlays
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
      case Some(x: SysClockNexysA7PlacedOverlay) => x.clock
    }
    val powerOnReset = PowerOnResetFPGAOnly(sysclk)
    sdc.addAsyncPath(Seq(powerOnReset))

    resetPin := reset_ibuf.io.O

    pllReset :=
      (!reset_ibuf.io.O) || powerOnReset //NexysA7 is active low reset
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