package sifive.fpgashells.shell.xilinx

import chisel3._
import chisel3.experimental.dataview._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.prci._
import org.chipsalliance.cde.config._
import sifive.fpgashells.clocks._
import sifive.fpgashells.ip.xilinx._
import sifive.fpgashells.shell._

class SysClockGeorgeFPGAPlacedOverlay(val shell: GeorgeFPGAShellBasicOverlays, name: String, val designInput: ClockInputDesignInput, val shellInput: ClockInputShellInput)
  extends SingleEndedClockInputXilinxPlacedOverlay(name, designInput, shellInput)
{
  val node = shell { ClockSourceNode(freqMHz = 50, jitterPS = 50) }

  shell { InModuleBody {
    val clk: Clock = io
    shell.xdc.addPackagePin(clk, "P17")
    shell.xdc.addIOStandard(clk, "LVCMOS33")
  } }
}
class SysClockGeorgeFPGAShellPlacer(val shell: GeorgeFPGAShellBasicOverlays, val shellInput: ClockInputShellInput)(implicit val valName: ValName)
  extends ClockInputShellPlacer[GeorgeFPGAShellBasicOverlays] {
  def place(designInput: ClockInputDesignInput) = new SysClockGeorgeFPGAPlacedOverlay(shell, valName.name, designInput, shellInput)
}

class SPIFlashGeorgeFPGAPlacedOverlay(val shell: GeorgeFPGAShellBasicOverlays, name: String, val designInput: SPIFlashDesignInput, val shellInput: SPIFlashShellInput)
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
class SPIFlashGeorgeFPGAShellPlacer(val shell: GeorgeFPGAShellBasicOverlays, val shellInput: SPIFlashShellInput)(implicit val valName: ValName)
  extends SPIFlashShellPlacer[GeorgeFPGAShellBasicOverlays] {
  def place(designInput: SPIFlashDesignInput) = new SPIFlashGeorgeFPGAPlacedOverlay(shell, valName.name, designInput, shellInput)
}

class UARTGeorgeFPGAPlacedOverlay(val shell: GeorgeFPGAShellBasicOverlays, name: String, val designInput: UARTDesignInput, val shellInput: UARTShellInput)
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
class UARTGeorgeFPGAShellPlacer(val shell: GeorgeFPGAShellBasicOverlays, val shellInput: UARTShellInput)(implicit val valName: ValName)
  extends UARTShellPlacer[GeorgeFPGAShellBasicOverlays] {
  def place(designInput: UARTDesignInput) = new UARTGeorgeFPGAPlacedOverlay(shell, valName.name, designInput, shellInput)
}

//LEDS - r0, g0, b0, 2 normal leds
object LEDGeorgeFPGAPinConstraints{
  val pins = Seq("L16", "L18")
}
class LEDGeorgeFPGAPlacedOverlay(val shell: GeorgeFPGAShellBasicOverlays, name: String, val designInput: LEDDesignInput, val shellInput: LEDShellInput)
  extends LEDXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(LEDGeorgeFPGAPinConstraints.pins(shellInput.number)))
class LEDGeorgeFPGAShellPlacer(val shell: GeorgeFPGAShellBasicOverlays, val shellInput: LEDShellInput)(implicit val valName: ValName)
  extends LEDShellPlacer[GeorgeFPGAShellBasicOverlays] {
  def place(designInput: LEDDesignInput) = new LEDGeorgeFPGAPlacedOverlay(shell, valName.name, designInput, shellInput)
}

class JTAGDebugBScanGeorgeFPGAPlacedOverlay(val shell: GeorgeFPGAShellBasicOverlays, name: String, val designInput: JTAGDebugBScanDesignInput, val shellInput: JTAGDebugBScanShellInput)
 extends JTAGDebugBScanXilinxPlacedOverlay(name, designInput, shellInput)
class JTAGDebugBScanGeorgeFPGAShellPlacer(val shell: GeorgeFPGAShellBasicOverlays, val shellInput: JTAGDebugBScanShellInput)(implicit val valName: ValName)
  extends JTAGDebugBScanShellPlacer[GeorgeFPGAShellBasicOverlays] {
  def place(designInput: JTAGDebugBScanDesignInput) = new JTAGDebugBScanGeorgeFPGAPlacedOverlay(shell, valName.name, designInput, shellInput)
}

// PMOD JD used for JTAG
class JTAGDebugGeorgeFPGAPlacedOverlay(val shell: GeorgeFPGAShellBasicOverlays, name: String, val designInput: JTAGDebugDesignInput, val shellInput: JTAGDebugShellInput)
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
class JTAGDebugGeorgeFPGAShellPlacer(val shell: GeorgeFPGAShellBasicOverlays, val shellInput: JTAGDebugShellInput)(implicit val valName: ValName)
  extends JTAGDebugShellPlacer[GeorgeFPGAShellBasicOverlays] {
  def place(designInput: JTAGDebugDesignInput) = new JTAGDebugGeorgeFPGAPlacedOverlay(shell, valName.name, designInput, shellInput)
}

//Core to shell external resets
class CTSResetGeorgeFPGAPlacedOverlay(val shell: GeorgeFPGAShellBasicOverlays, name: String, val designInput: CTSResetDesignInput, val shellInput: CTSResetShellInput)
  extends CTSResetPlacedOverlay(name, designInput, shellInput)
class CTSResetGeorgeFPGAShellPlacer(val shell: GeorgeFPGAShellBasicOverlays, val shellInput: CTSResetShellInput)(implicit val valName: ValName)
  extends CTSResetShellPlacer[GeorgeFPGAShellBasicOverlays] {
  def place(designInput: CTSResetDesignInput) = new CTSResetGeorgeFPGAPlacedOverlay(shell, valName.name, designInput, shellInput)
}


abstract class GeorgeFPGAShellBasicOverlays()(implicit p: Parameters) extends Series7Shell {
  // Order matters; ddr depends on sys_clock
  val sys_clock = Overlay(ClockInputOverlayKey, new SysClockGeorgeFPGAShellPlacer(this, ClockInputShellInput()))
  val led       = Seq.tabulate(2)(i => Overlay(LEDOverlayKey, new LEDGeorgeFPGAShellPlacer(this, LEDMetas(i))(valName = ValName(s"led_$i"))))
  // val uart      = Overlay(UARTOverlayKey, new UARTGeorgeFPGAShellPlacer(this, UARTShellInput()))
  // val sdio      = Overlay(SPIOverlayKey, new SDIOGeorgeFPGAShellPlacer(this, SPIShellInput()))
  // val jtag      = Overlay(JTAGDebugOverlayKey, new JTAGDebugGeorgeFPGAShellPlacer(this, JTAGDebugShellInput()))
  // val cjtag     = Overlay(cJTAGDebugOverlayKey, new cJTAGDebugGeorgeFPGAShellPlacer(this, cJTAGDebugShellInput()))
  // val spi_flash = Overlay(SPIFlashOverlayKey, new SPIFlashGeorgeFPGAShellPlacer(this, SPIFlashShellInput()))
  val cts_reset = Overlay(CTSResetOverlayKey, new CTSResetGeorgeFPGAShellPlacer(this, CTSResetShellInput()))
  // val jtagBScan = Overlay(JTAGDebugBScanOverlayKey, new JTAGDebugBScanGeorgeFPGAShellPlacer(this, JTAGDebugBScanShellInput()))

  def LEDMetas(i: Int): LEDShellInput =
    LEDShellInput(
      color = if((i < 12) && (i % 3 == 1)) "green" else if((i < 12) && (i % 3 == 2)) "blue" else "red",
      rgb = (i < 12),
      number = i)
}

class GeorgeFPGAShell()(implicit p: Parameters) extends GeorgeFPGAShellBasicOverlays
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
    xdc.addPackagePin(reset, "R13")
    xdc.addIOStandard(reset, "LVCMOS33")

    val reset_ibuf = Module(new IBUF)
    reset_ibuf.io.I := reset
    val sysclk: Clock = sys_clock.get() match {
      case Some(x: SysClockGeorgeFPGAPlacedOverlay) => x.clock
    }
    val powerOnReset = PowerOnResetFPGAOnly(sysclk)
    sdc.addAsyncPath(Seq(powerOnReset))

    resetPin := reset_ibuf.io.O

    pllReset :=
      (~reset_ibuf.io.O) || powerOnReset //GeorgeFPGA is active low reset
  }
}

// class GeorgeFPGAShellGPIOPMOD()(implicit p: Parameters) extends GeorgeFPGAShellBasicOverlays
// //This is the Shell used for coreip arty builds, with GPIOS and trace signals on the pmods
// {
//   // PLL reset causes
//   val pllReset = InModuleBody { Wire(Bool()) }

//   val gpio_pmod = Overlay(GPIOPMODOverlayKey, new GPIOPMODGeorgeFPGAShellPlacer(this, GPIOPMODShellInput()))
//   val trace_pmod = Overlay(TracePMODOverlayKey, new TracePMODGeorgeFPGAShellPlacer(this, TracePMODShellInput()))

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
//       case Some(x: SysClockGeorgeFPGAPlacedOverlay) => x.clock
//     }
//     val powerOnReset = PowerOnResetFPGAOnly(sysclk)
//     sdc.addAsyncPath(Seq(powerOnReset))
//     val ctsReset: Bool = cts_reset.get() match {
//       case Some(x: CTSResetGeorgeFPGAPlacedOverlay) => x.designInput.rst
//       case None => false.B
//     }

//     pllReset :=
//       (!reset_ibuf.io.O) || powerOnReset || ctsReset //GeorgeFPGA is active low reset
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
