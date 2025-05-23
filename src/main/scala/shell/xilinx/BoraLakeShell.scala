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

class SysClockBoraLakePlacedOverlay(val shell: BoraLakeShellBasicOverlays, name: String, val designInput: ClockInputDesignInput, val shellInput: ClockInputShellInput)
  extends SingleEndedClockInputXilinxPlacedOverlay(name, designInput, shellInput)
{
  val node = shell { ClockSourceNode(freqMHz = 50, jitterPS = 50) }

  shell { InModuleBody {
    val clk: Clock = io
    shell.xdc.addPackagePin(clk, "F22")
    shell.xdc.addIOStandard(clk, "LVCMOS33")
  } }
}
class SysClockBoraLakeShellPlacer(val shell: BoraLakeShellBasicOverlays, val shellInput: ClockInputShellInput)(implicit val valName: ValName)
  extends ClockInputShellPlacer[BoraLakeShellBasicOverlays] {
  def place(designInput: ClockInputDesignInput) = new SysClockBoraLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}

class SPIFlashBoraLakePlacedOverlay(val shell: BoraLakeShellBasicOverlays, name: String, val designInput: SPIFlashDesignInput, val shellInput: SPIFlashShellInput)
  extends SPIFlashXilinxPlacedOverlay(name, designInput, shellInput)
{

  shell { InModuleBody {
    val packagePinsWithPackageIOs = Seq(
      ("C8",  IOPin(io.qspi_sck)),
      ("C23", IOPin(io.qspi_cs)),
      ("B24", IOPin(io.qspi_dq(0))),
      ("A25", IOPin(io.qspi_dq(1))),
      ("B22", IOPin(io.qspi_dq(2))),
      ("A22", IOPin(io.qspi_dq(3))))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
    } }
    packagePinsWithPackageIOs drop 1 foreach { case (pin, io) => {
      shell.xdc.addPullup(io)
    } }
  } }
}
class SPIFlashBoraLakeShellPlacer(val shell: BoraLakeShellBasicOverlays, val shellInput: SPIFlashShellInput)(implicit val valName: ValName)
  extends SPIFlashShellPlacer[BoraLakeShellBasicOverlays] {
  def place(designInput: SPIFlashDesignInput) = new SPIFlashBoraLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}

class UARTBoraLakePlacedOverlay(val shell: BoraLakeShellBasicOverlays, name: String, val designInput: UARTDesignInput, val shellInput: UARTShellInput)
  extends UARTXilinxPlacedOverlay(name, designInput, shellInput, false)
{
  shell { InModuleBody {
    val packagePinsWithPackageIOs = Seq(
      ("A24", IOPin(io.rxd)),
      ("A23", IOPin(io.txd)))

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
      shell.xdc.addIOB(io)
    } }
  } }
}
class UARTBoraLakeShellPlacer(val shell: BoraLakeShellBasicOverlays, val shellInput: UARTShellInput)(implicit val valName: ValName)
  extends UARTShellPlacer[BoraLakeShellBasicOverlays] {
  def place(designInput: UARTDesignInput) = new UARTBoraLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}

//LEDS - r0, g0, b0, 2 normal leds
object LEDBoraLakePinConstraints{
  val pins = Seq("F23", "E22", "D24", "D23", "G22", "E21")
}
class LEDBoraLakePlacedOverlay(val shell: BoraLakeShellBasicOverlays, name: String, val designInput: LEDDesignInput, val shellInput: LEDShellInput)
  extends LEDXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(LEDBoraLakePinConstraints.pins(shellInput.number)))
class LEDBoraLakeShellPlacer(val shell: BoraLakeShellBasicOverlays, val shellInput: LEDShellInput)(implicit val valName: ValName)
  extends LEDShellPlacer[BoraLakeShellBasicOverlays] {
  def place(designInput: LEDDesignInput) = new LEDBoraLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}

//Buttons
object ButtonBoraLakePinConstraints {
  val pins = Seq("G24", "F24", "E25", "D25", "G25")
}
class ButtonBoraLakePlacedOverlay(val shell: BoraLakeShellBasicOverlays, name: String, val designInput: ButtonDesignInput, val shellInput: ButtonShellInput)
  extends ButtonXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(ButtonBoraLakePinConstraints.pins(shellInput.number)))
class ButtonBoraLakeShellPlacer(val shell: BoraLakeShellBasicOverlays, val shellInput: ButtonShellInput)(implicit val valName: ValName)
  extends ButtonShellPlacer[BoraLakeShellBasicOverlays] {
  def place(designInput: ButtonDesignInput) = new ButtonBoraLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}

class JTAGDebugBScanBoraLakePlacedOverlay(val shell: BoraLakeShellBasicOverlays, name: String, val designInput: JTAGDebugBScanDesignInput, val shellInput: JTAGDebugBScanShellInput)
 extends JTAGDebugBScanXilinxPlacedOverlay(name, designInput, shellInput)
class JTAGDebugBScanBoraLakeShellPlacer(val shell: BoraLakeShellBasicOverlays, val shellInput: JTAGDebugBScanShellInput)(implicit val valName: ValName)
  extends JTAGDebugBScanShellPlacer[BoraLakeShellBasicOverlays] {
  def place(designInput: JTAGDebugBScanDesignInput) = new JTAGDebugBScanBoraLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}

// PMOD JD used for JTAG
class JTAGDebugBoraLakePlacedOverlay(val shell: BoraLakeShellBasicOverlays, name: String, val designInput: JTAGDebugDesignInput, val shellInput: JTAGDebugShellInput)
  extends JTAGDebugXilinxPlacedOverlay(name, designInput, shellInput)
{
  shell { InModuleBody {
    shell.sdc.addClock("JTCK", IOPin(io.jtag_TCK), 10)
    shell.sdc.addGroup(clocks = Seq("JTCK"))
    shell.xdc.clockDedicatedRouteFalse(IOPin(io.jtag_TCK))
    val packagePinsWithPackageIOs = Seq(
      ("H22", IOPin(io.jtag_TCK)),  //pin JD-3
      ("J24", IOPin(io.jtag_TMS)),  //pin JD-8
      ("J25", IOPin(io.jtag_TDI)),  //pin JD-7
      ("L22", IOPin(io.jtag_TDO)),  //pin JD-1
      ("K22", IOPin(io.srst_n))
      
      )

    packagePinsWithPackageIOs foreach { case (pin, io) => {
      shell.xdc.addPackagePin(io, pin)
      shell.xdc.addIOStandard(io, "LVCMOS33")
      shell.xdc.addPullup(io)
    } }
  } }
}
class JTAGDebugBoraLakeShellPlacer(val shell: BoraLakeShellBasicOverlays, val shellInput: JTAGDebugShellInput)(implicit val valName: ValName)
  extends JTAGDebugShellPlacer[BoraLakeShellBasicOverlays] {
  def place(designInput: JTAGDebugDesignInput) = new JTAGDebugBoraLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}

//Core to shell external resets
class CTSResetBoraLakePlacedOverlay(val shell: BoraLakeShellBasicOverlays, name: String, val designInput: CTSResetDesignInput, val shellInput: CTSResetShellInput)
  extends CTSResetPlacedOverlay(name, designInput, shellInput)
class CTSResetBoraLakeShellPlacer(val shell: BoraLakeShellBasicOverlays, val shellInput: CTSResetShellInput)(implicit val valName: ValName)
  extends CTSResetShellPlacer[BoraLakeShellBasicOverlays] {
  def place(designInput: CTSResetDesignInput) = new CTSResetBoraLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}



case object BoraLakeDDRSize extends Field[BigInt](0x40000000L * 4) // 4GB
class DDRBoraLakePlacedOverlay(val shell: BoraLakeShellBasicOverlays, name: String, val designInput: DDRDesignInput, val shellInput: DDRShellInput)
  extends DDRPlacedOverlay[XilinxBoraLakeMIGPads](name, designInput, shellInput)
{
  val size = p(BoraLakeDDRSize)

  val ddrClk1 = shell { ClockSinkNode(freqMHz = 166.666)}
  val ddrClk2 = shell { ClockSinkNode(freqMHz = 200)}
  val ddrGroup = shell { ClockGroup() }
  ddrClk1 := di.wrangler := ddrGroup := di.corePLL
  ddrClk2 := di.wrangler := ddrGroup
  
  val migParams = XilinxBoraLakeMIGParams(address = AddressSet.misaligned(di.baseAddress, size))
  val mig = LazyModule(new XilinxBoraLakeMIG(migParams))
  val ddrUI     = shell { ClockSourceNode(freqMHz = 100) }
  val areset    = shell { ClockSinkNode(Seq(ClockSinkParameters())) }
  areset := di.wrangler := ddrUI

  def overlayOutput = DDROverlayOutput(ddr = mig.node)
  def ioFactory = new XilinxBoraLakeMIGPads(size)

  shell { InModuleBody {
    require (shell.sys_clock.get.isDefined, "Use of DDRBoraLakePlacedOverlay depends on SysClockBoraLakePlacedOverlay")
    val (sys, _) = shell.sys_clock.get.get.overlayOutput.node.out(0)
    val (ui, _) = ddrUI.out(0)
    val (dclk1, _) = ddrClk1.in(0)
    val (dclk2, _) = ddrClk2.in(0)
    val (ar, _) = areset.in(0)
    val port = mig.module.io.port
    
    io <> port.viewAsSupertype(new XilinxBoraLakeMIGPads(mig.depth))
    ui.clock := port.ui_clk
    ui.reset := !port.mmcm_locked || port.ui_clk_sync_rst
    port.sys_clk_i := dclk1.clock.asUInt
    port.clk_ref_i := dclk2.clock.asUInt
    port.sys_rst := shell.pllReset
    port.aresetn := !(ar.reset.asBool)
  } }

  shell.sdc.addGroup(clocks = Seq("clk_pll_i"), pins = Seq(mig.island.module.blackbox.io.ui_clk))
}
class DDRBoraLakeShellPlacer(val shell: BoraLakeShellBasicOverlays, val shellInput: DDRShellInput)(implicit val valName: ValName)
  extends DDRShellPlacer[BoraLakeShellBasicOverlays] {
  def place(designInput: DDRDesignInput) = new DDRBoraLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}


abstract class BoraLakeShellBasicOverlays()(implicit p: Parameters) extends Series7Shell {
  // Order matters; ddr depends on sys_clock
  val sys_clock = Overlay(ClockInputOverlayKey, new SysClockBoraLakeShellPlacer(this, ClockInputShellInput()))
  val led       = Seq.tabulate(6)(i => Overlay(LEDOverlayKey, new LEDBoraLakeShellPlacer(this, LEDMetas(i))(valName = ValName(s"led_$i"))))
  val button    = Seq.tabulate(5)(i => Overlay(ButtonOverlayKey, new ButtonBoraLakeShellPlacer(this, ButtonShellInput(number = i))(valName = ValName(s"button_$i"))))
  val ddr       = Overlay(DDROverlayKey, new DDRBoraLakeShellPlacer(this, DDRShellInput()))


  // val uart      = Overlay(UARTOverlayKey, new UARTBoraLakeShellPlacer(this, UARTShellInput()))
  // val sdio      = Overlay(SPIOverlayKey, new SDIOBoraLakeShellPlacer(this, SPIShellInput()))
  // val jtag      = Overlay(JTAGDebugOverlayKey, new JTAGDebugBoraLakeShellPlacer(this, JTAGDebugShellInput()))
  // val cjtag     = Overlay(cJTAGDebugOverlayKey, new cJTAGDebugBoraLakeShellPlacer(this, cJTAGDebugShellInput()))
  // val spi_flash = Overlay(SPIFlashOverlayKey, new SPIFlashBoraLakeShellPlacer(this, SPIFlashShellInput()))
  val cts_reset = Overlay(CTSResetOverlayKey, new CTSResetBoraLakeShellPlacer(this, CTSResetShellInput()))
  // val jtagBScan = Overlay(JTAGDebugBScanOverlayKey, new JTAGDebugBScanBoraLakeShellPlacer(this, JTAGDebugBScanShellInput()))

  def LEDMetas(i: Int): LEDShellInput =
    LEDShellInput(
      color = if((i < 12) && (i % 3 == 1)) "green" else if((i < 12) && (i % 3 == 2)) "blue" else "red",
      rgb = (i < 12),
      number = i)
}

class BoraLakeShell()(implicit p: Parameters) extends BoraLakeShellBasicOverlays
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
    xdc.addPackagePin(reset, "C26")
    xdc.addIOStandard(reset, "LVCMOS33")

    val reset_ibuf = Module(new IBUF)
    reset_ibuf.io.I := reset
    val sysclk: Clock = sys_clock.get() match {
      case Some(x: SysClockBoraLakePlacedOverlay) => x.clock
    }
    val powerOnReset = PowerOnResetFPGAOnly(sysclk)
    sdc.addAsyncPath(Seq(powerOnReset))

    resetPin := reset_ibuf.io.O

    pllReset :=
      (~reset_ibuf.io.O) || powerOnReset //BoraLake is active low reset
  }
}
