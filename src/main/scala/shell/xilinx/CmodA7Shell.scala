package sifive.fpgashells.shell.xilinx

import chisel3._
import chisel3.experimental.dataview._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.prci._
import org.chipsalliance.cde.config._
import sifive.fpgashells.clocks._
import sifive.fpgashells.ip.xilinx._
import sifive.fpgashells.shell._

class SysClockCmodA7PlacedOverlay(val shell: CmodA7ShellBasicOverlays, name: String, val designInput: ClockInputDesignInput, val shellInput: ClockInputShellInput)
  extends SingleEndedClockInputXilinxPlacedOverlay(name, designInput, shellInput)
{
  val node = shell { ClockSourceNode(freqMHz = 12, jitterPS = 50) }

  shell { InModuleBody {
    val clk: Clock = io
    shell.xdc.addPackagePin(clk, "L17")
    shell.xdc.addIOStandard(clk, "LVCMOS33")
  } }
}
class SysClockCmodA7ShellPlacer(val shell: CmodA7ShellBasicOverlays, val shellInput: ClockInputShellInput)(implicit val valName: ValName)
  extends ClockInputShellPlacer[CmodA7ShellBasicOverlays] {
  def place(designInput: ClockInputDesignInput) = new SysClockCmodA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class SPIFlashCmodA7PlacedOverlay(val shell: CmodA7ShellBasicOverlays, name: String, val designInput: SPIFlashDesignInput, val shellInput: SPIFlashShellInput)
  extends SPIFlashXilinxPlacedOverlay(name, designInput, shellInput)
{

  shell { InModuleBody {
    val packagePinsWithPackageIOs = Seq(("C11", IOPin(io.qspi_sck)),
      ("K19", IOPin(io.qspi_cs)),
      ("D18", IOPin(io.qspi_dq(0))),
      ("D19", IOPin(io.qspi_dq(1))),
      ("G18", IOPin(io.qspi_dq(2))),
      ("F18", IOPin(io.qspi_dq(3))))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
    } }
    packagePinsWithPackageIOs drop 1 foreach { case (pin, io) => {
      shell.xdc.addPullup(io)
    } }
  } }
}
class SPIFlashCmodA7ShellPlacer(val shell: CmodA7ShellBasicOverlays, val shellInput: SPIFlashShellInput)(implicit val valName: ValName)
  extends SPIFlashShellPlacer[CmodA7ShellBasicOverlays] {
  def place(designInput: SPIFlashDesignInput) = new SPIFlashCmodA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class UARTCmodA7PlacedOverlay(val shell: CmodA7ShellBasicOverlays, name: String, val designInput: UARTDesignInput, val shellInput: UARTShellInput)
  extends UARTXilinxPlacedOverlay(name, designInput, shellInput, false)
{
  shell { InModuleBody {
    val packagePinsWithPackageIOs = Seq(("A9", IOPin(io.rxd)),
      ("D10", IOPin(io.txd)))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
      shell.xdc.addIOB(io)
    } }
  } }
}
class UARTCmodA7ShellPlacer(val shell: CmodA7ShellBasicOverlays, val shellInput: UARTShellInput)(implicit val valName: ValName)
  extends UARTShellPlacer[CmodA7ShellBasicOverlays] {
  def place(designInput: UARTDesignInput) = new UARTCmodA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//LEDS - r0, g0, b0, 2 normal leds
object LEDCmodA7PinConstraints{
  val pins = Seq("C17", "B16", "B17", "A17", "C16")
}
class LEDCmodA7PlacedOverlay(val shell: CmodA7ShellBasicOverlays, name: String, val designInput: LEDDesignInput, val shellInput: LEDShellInput)
  extends LEDXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(LEDCmodA7PinConstraints.pins(shellInput.number)))
class LEDCmodA7ShellPlacer(val shell: CmodA7ShellBasicOverlays, val shellInput: LEDShellInput)(implicit val valName: ValName)
  extends LEDShellPlacer[CmodA7ShellBasicOverlays] {
  def place(designInput: LEDDesignInput) = new LEDCmodA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

class JTAGDebugBScanCmodA7PlacedOverlay(val shell: CmodA7ShellBasicOverlays, name: String, val designInput: JTAGDebugBScanDesignInput, val shellInput: JTAGDebugBScanShellInput)
 extends JTAGDebugBScanXilinxPlacedOverlay(name, designInput, shellInput)
class JTAGDebugBScanCmodA7ShellPlacer(val shell: CmodA7ShellBasicOverlays, val shellInput: JTAGDebugBScanShellInput)(implicit val valName: ValName)
  extends JTAGDebugBScanShellPlacer[CmodA7ShellBasicOverlays] {
  def place(designInput: JTAGDebugBScanDesignInput) = new JTAGDebugBScanCmodA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

// PMOD JD used for JTAG
class JTAGDebugCmodA7PlacedOverlay(val shell: CmodA7ShellBasicOverlays, name: String, val designInput: JTAGDebugDesignInput, val shellInput: JTAGDebugShellInput)
  extends JTAGDebugXilinxPlacedOverlay(name, designInput, shellInput)
{
  shell { InModuleBody {
    shell.sdc.addClock("JTCK", IOPin(io.jtag_TCK), 10)
    shell.sdc.addGroup(clocks = Seq("JTCK"))
    shell.xdc.clockDedicatedRouteFalse(IOPin(io.jtag_TCK))
    val packagePinsWithPackageIOs = Seq(("F4", IOPin(io.jtag_TCK)),  //pin JD-3
      ("D2", IOPin(io.jtag_TMS)),  //pin JD-8
      ("E2", IOPin(io.jtag_TDI)),  //pin JD-7
      ("D4", IOPin(io.jtag_TDO)),  //pin JD-1
      ("H2", IOPin(io.srst_n)))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
      shell.xdc.addPullup(io)
    } }
  } }
}
class JTAGDebugCmodA7ShellPlacer(val shell: CmodA7ShellBasicOverlays, val shellInput: JTAGDebugShellInput)(implicit val valName: ValName)
  extends JTAGDebugShellPlacer[CmodA7ShellBasicOverlays] {
  def place(designInput: JTAGDebugDesignInput) = new JTAGDebugCmodA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}

//Core to shell external resets
class CTSResetCmodA7PlacedOverlay(val shell: CmodA7ShellBasicOverlays, name: String, val designInput: CTSResetDesignInput, val shellInput: CTSResetShellInput)
  extends CTSResetPlacedOverlay(name, designInput, shellInput)
class CTSResetCmodA7ShellPlacer(val shell: CmodA7ShellBasicOverlays, val shellInput: CTSResetShellInput)(implicit val valName: ValName)
  extends CTSResetShellPlacer[CmodA7ShellBasicOverlays] {
  def place(designInput: CTSResetDesignInput) = new CTSResetCmodA7PlacedOverlay(shell, valName.name, designInput, shellInput)
}


abstract class CmodA7ShellBasicOverlays()(implicit p: Parameters) extends Series7Shell {
  // Order matters; ddr depends on sys_clock
  val sys_clock = Overlay(ClockInputOverlayKey, new SysClockCmodA7ShellPlacer(this, ClockInputShellInput()))
  val led       = Seq.tabulate(5)(i => Overlay(LEDOverlayKey, new LEDCmodA7ShellPlacer(this, LEDMetas(i))(valName = ValName(s"led_$i"))))
  // val uart      = Overlay(UARTOverlayKey, new UARTCmodA7ShellPlacer(this, UARTShellInput()))
  // val sdio      = Overlay(SPIOverlayKey, new SDIOCmodA7ShellPlacer(this, SPIShellInput()))
  // val jtag      = Overlay(JTAGDebugOverlayKey, new JTAGDebugCmodA7ShellPlacer(this, JTAGDebugShellInput()))
  // val cjtag     = Overlay(cJTAGDebugOverlayKey, new cJTAGDebugCmodA7ShellPlacer(this, cJTAGDebugShellInput()))
  // val spi_flash = Overlay(SPIFlashOverlayKey, new SPIFlashCmodA7ShellPlacer(this, SPIFlashShellInput()))
  val cts_reset = Overlay(CTSResetOverlayKey, new CTSResetCmodA7ShellPlacer(this, CTSResetShellInput()))
  // val jtagBScan = Overlay(JTAGDebugBScanOverlayKey, new JTAGDebugBScanCmodA7ShellPlacer(this, JTAGDebugBScanShellInput()))

  def LEDMetas(i: Int): LEDShellInput =
    LEDShellInput(
      color = if((i < 12) && (i % 3 == 1)) "green" else if((i < 12) && (i % 3 == 2)) "blue" else "red",
      rgb = (i < 12),
      number = i)
}

class CmodA7Shell()(implicit p: Parameters) extends CmodA7ShellBasicOverlays
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
    xdc.addPackagePin(reset, "A18")
    xdc.addIOStandard(reset, "LVCMOS33")

    val reset_ibuf = Module(new IBUF)
    reset_ibuf.io.I := reset
    val sysclk: Clock = sys_clock.get() match {
      case Some(x: SysClockCmodA7PlacedOverlay) => x.clock
    }
    val powerOnReset = PowerOnResetFPGAOnly(sysclk)
    sdc.addAsyncPath(Seq(powerOnReset))

    resetPin := ~reset_ibuf.io.O

    pllReset :=
      (!reset_ibuf.io.O) || powerOnReset //CmodA7 is active low reset
  }
}

// class CmodA7ShellGPIOPMOD()(implicit p: Parameters) extends CmodA7ShellBasicOverlays
// //This is the Shell used for coreip arty builds, with GPIOS and trace signals on the pmods
// {
//   // PLL reset causes
//   val pllReset = InModuleBody { Wire(Bool()) }

//   val gpio_pmod = Overlay(GPIOPMODOverlayKey, new GPIOPMODCmodA7ShellPlacer(this, GPIOPMODShellInput()))
//   val trace_pmod = Overlay(TracePMODOverlayKey, new TracePMODCmodA7ShellPlacer(this, TracePMODShellInput()))

//   val topDesign = LazyModule(p(DesignKey)(designParameters))

//   // Place the sys_clock at the Shell if the user didn't ask for it
//   p(ClockInputOverlayKey).foreach(_.place(ClockInputDesignInput()))

//   override lazy val module = new LazyRawModuleImp(this) {
//     override def provideImplicitClockToLazyChildren = true
//     val reset = IO(Input(Bool()))
//     xdc.addBoardPin(reset, "reset")

//     val reset_ibuf = Module(new IBUF)
//     reset_ibuf.io.I := reset

//     val sysclk: Clock = sys_clock.get() match {
//       case Some(x: SysClockCmodA7PlacedOverlay) => x.clock
//     }
//     val powerOnReset = PowerOnResetFPGAOnly(sysclk)
//     sdc.addAsyncPath(Seq(powerOnReset))
//     val ctsReset: Bool = cts_reset.get() match {
//       case Some(x: CTSResetCmodA7PlacedOverlay) => x.designInput.rst
//       case None => false.B
//     }

//     pllReset :=
//       (!reset_ibuf.io.O) || powerOnReset || ctsReset //CmodA7 is active low reset
//   }
// }

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
