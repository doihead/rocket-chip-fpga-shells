package sifive.fpgashells.shell.xilinx

import chisel3._
import chisel3.experimental.dataview._
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.prci._
import org.chipsalliance.cde.config._
import sifive.fpgashells.clocks._
import sifive.fpgashells.ip.xilinx._
import sifive.fpgashells.shell._
import freechips.rocketchip.util.ElaborationArtefacts

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
    val packagePinsWithPackageIOs = Seq(("D12", IOPin(io.qspi_sck)),
      ("B11", IOPin(io.qspi_cs)),
      ("A11", IOPin(io.qspi_dq(0))),
      ("D13", IOPin(io.qspi_dq(1))),
      ("B18", IOPin(io.qspi_dq(2))),
      ("G13", IOPin(io.qspi_dq(3))))

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
    val packagePinsWithPackageIOs = Seq(
      ("F4", IOPin(io.jtag_TCK)),  //pin JD-3
      ("D2", IOPin(io.jtag_TMS)),  //pin JD-8
      ("E2", IOPin(io.jtag_TDI)),  //pin JD-7
      ("D4", IOPin(io.jtag_TDO)),  //pin JD-1
      ("H2", IOPin(io.srst_n))
      
      )

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



case object GeorgeFPGADDRSize extends Field[BigInt](0x10000000L * 1) // 256 MB
class DDRGeorgeFPGAPlacedOverlay(val shell: GeorgeFPGAShellBasicOverlays, name: String, val designInput: DDRDesignInput, val shellInput: DDRShellInput)
  extends DDRPlacedOverlay[XilinxGeorgeFPGAMIGPads](name, designInput, shellInput)
{
  val size = p(GeorgeFPGADDRSize)

  val ddrClk1 = shell { ClockSinkNode(freqMHz = 166.666)}
  val ddrClk2 = shell { ClockSinkNode(freqMHz = 200)}
  val ddrGroup = shell { ClockGroup() }
  ddrClk1 := di.wrangler := ddrGroup := di.corePLL
  ddrClk2 := di.wrangler := ddrGroup
  
  val migParams = XilinxGeorgeFPGAMIGParams(address = AddressSet.misaligned(di.baseAddress, size))
  val mig = LazyModule(new XilinxGeorgeFPGAMIG(migParams))
  val ddrUI     = shell { ClockSourceNode(freqMHz = 100) }
  val areset    = shell { ClockSinkNode(Seq(ClockSinkParameters())) }
  areset := di.wrangler := ddrUI

  def overlayOutput = DDROverlayOutput(ddr = mig.node)
  def ioFactory = new XilinxGeorgeFPGAMIGPads(size)

  shell { InModuleBody {
    require (shell.sys_clock.get.isDefined, "Use of DDRGeorgeFPGAPlacedOverlay depends on SysClockGeorgeFPGAPlacedOverlay")
    val (sys, _) = shell.sys_clock.get.get.overlayOutput.node.out(0)
    val (ui, _) = ddrUI.out(0)
    val (dclk1, _) = ddrClk1.in(0)
    val (dclk2, _) = ddrClk2.in(0)
    val (ar, _) = areset.in(0)
    val port = mig.module.io.port
    
    io <> port.viewAsSupertype(new XilinxGeorgeFPGAMIGPads(mig.depth))
    ui.clock := port.ui_clk
    ui.reset := !port.mmcm_locked || port.ui_clk_sync_rst
    port.sys_clk_i := dclk1.clock.asUInt
    port.clk_ref_i := dclk2.clock.asUInt
    port.sys_rst := shell.pllReset
    port.aresetn := !(ar.reset.asBool)
  } }

  shell.sdc.addGroup(clocks = Seq("clk_pll_i"), pins = Seq(mig.island.module.blackbox.io.ui_clk))
}
class DDRGeorgeFPGAShellPlacer(val shell: GeorgeFPGAShellBasicOverlays, val shellInput: DDRShellInput)(implicit val valName: ValName)
  extends DDRShellPlacer[GeorgeFPGAShellBasicOverlays] {
  def place(designInput: DDRDesignInput) = new DDRGeorgeFPGAPlacedOverlay(shell, valName.name, designInput, shellInput)
}


abstract class GeorgeFPGAShellBasicOverlays()(implicit p: Parameters) extends Series7Shell {
  // Order matters; ddr depends on sys_clock
  val sys_clock = Overlay(ClockInputOverlayKey, new SysClockGeorgeFPGAShellPlacer(this, ClockInputShellInput()))
  val led       = Seq.tabulate(2)(i => Overlay(LEDOverlayKey, new LEDGeorgeFPGAShellPlacer(this, LEDMetas(i))(valName = ValName(s"led_$i"))))
  val ddr       = Overlay(DDROverlayKey, new DDRGeorgeFPGAShellPlacer(this, DDRShellInput()))
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
    xdc.addPackagePin(reset, "T18")
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
