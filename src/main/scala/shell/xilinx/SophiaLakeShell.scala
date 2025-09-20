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
import freechips.rocketchip.util.{ElaborationArtefacts, SyncResetSynchronizerShiftReg}

class SysClockSophiaLakePlacedOverlay(val shell: SophiaLakeShellBasicOverlays, name: String, val designInput: ClockInputDesignInput, val shellInput: ClockInputShellInput)
  extends SingleEndedClockInputXilinxPlacedOverlay(name, designInput, shellInput)
{
  val node = shell { ClockSourceNode(freqMHz = 50, jitterPS = 50) }

  shell { InModuleBody {
    val clk: Clock = io
    shell.xdc.addPackagePin(clk, "V20")
    shell.xdc.addIOStandard(clk, "LVCMOS33")
    shell.xdc.clockDedicatedRouteFalse(clk)
  } }
}
class SysClockSophiaLakeShellPlacer(val shell: SophiaLakeShellBasicOverlays, val shellInput: ClockInputShellInput)(implicit val valName: ValName)
  extends ClockInputShellPlacer[SophiaLakeShellBasicOverlays] {
  def place(designInput: ClockInputDesignInput) = new SysClockSophiaLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}

//LEDS - r0, g0, b0, 2 normal leds
object LEDSophiaLakePinConstraints{
  val pins = Seq("V18", "Y18", "P19", "U20")
}
class LEDSophiaLakePlacedOverlay(val shell: SophiaLakeShellBasicOverlays, name: String, val designInput: LEDDesignInput, val shellInput: LEDShellInput)
  extends LEDXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(LEDSophiaLakePinConstraints.pins(shellInput.number)))
class LEDSophiaLakeShellPlacer(val shell: SophiaLakeShellBasicOverlays, val shellInput: LEDShellInput)(implicit val valName: ValName)
  extends LEDShellPlacer[SophiaLakeShellBasicOverlays] {
  def place(designInput: LEDDesignInput) = new LEDSophiaLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}

//Buttons
object ButtonSophiaLakePinConstraints {
  val pins = Seq("Y19", "Y21")
}
class ButtonSophiaLakePlacedOverlay(val shell: SophiaLakeShellBasicOverlays, name: String, val designInput: ButtonDesignInput, val shellInput: ButtonShellInput)
  extends ButtonXilinxPlacedOverlay(name, designInput, shellInput, packagePin = Some(ButtonSophiaLakePinConstraints.pins(shellInput.number)))
class ButtonSophiaLakeShellPlacer(val shell: SophiaLakeShellBasicOverlays, val shellInput: ButtonShellInput)(implicit val valName: ValName)
  extends ButtonShellPlacer[SophiaLakeShellBasicOverlays] {
  def place(designInput: ButtonDesignInput) = new ButtonSophiaLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}


//Core to shell external resets
class CTSResetSophiaLakePlacedOverlay(val shell: SophiaLakeShellBasicOverlays, name: String, val designInput: CTSResetDesignInput, val shellInput: CTSResetShellInput)
  extends CTSResetPlacedOverlay(name, designInput, shellInput)
class CTSResetSophiaLakeShellPlacer(val shell: SophiaLakeShellBasicOverlays, val shellInput: CTSResetShellInput)(implicit val valName: ValName)
  extends CTSResetShellPlacer[SophiaLakeShellBasicOverlays] {
  def place(designInput: CTSResetDesignInput) = new CTSResetSophiaLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}



case object SophiaLakeDDRSize extends Field[BigInt](0x40000000L * 1) // 1 GB
class DDRSophiaLakePlacedOverlay(val shell: SophiaLakeShellBasicOverlays, name: String, val designInput: DDRDesignInput, val shellInput: DDRShellInput)
  extends DDRPlacedOverlay[XilinxSophiaLakeMIGPads](name, designInput, shellInput)
{
  val size = p(SophiaLakeDDRSize)

  val ddrClk1 = shell { ClockSinkNode(freqMHz = 166.666)}
  val ddrClk2 = shell { ClockSinkNode(freqMHz = 200)}
  val ddrGroup = shell { ClockGroup() }
  ddrClk1 := di.wrangler := ddrGroup := di.corePLL
  ddrClk2 := di.wrangler := ddrGroup
  
  val migParams = XilinxSophiaLakeMIGParams(address = AddressSet.misaligned(di.baseAddress, size))
  val mig = LazyModule(new XilinxSophiaLakeMIG(migParams))
  val ddrUI     = shell { ClockSourceNode(freqMHz = 100) }
  val areset    = shell { ClockSinkNode(Seq(ClockSinkParameters())) }
  areset := di.wrangler := ddrUI

  def overlayOutput = DDROverlayOutput(ddr = mig.node)
  def ioFactory = new XilinxSophiaLakeMIGPads(size)

  shell { InModuleBody {
    require (shell.sys_clock.get.isDefined, "Use of DDRSophiaLakePlacedOverlay depends on SysClockSophiaLakePlacedOverlay")
    val (sys, _) = shell.sys_clock.get.get.overlayOutput.node.out(0)
    val (ui, _) = ddrUI.out(0)
    val (dclk1, _) = ddrClk1.in(0)
    val (dclk2, _) = ddrClk2.in(0)
    val (ar, _) = areset.in(0)
    val port = mig.module.io.port
    
    io <> port.viewAsSupertype(new XilinxSophiaLakeMIGPads(mig.depth))
    ui.clock := port.ui_clk
    ui.reset := !port.mmcm_locked || port.ui_clk_sync_rst
    port.sys_clk_i := dclk1.clock.asUInt
    port.clk_ref_i := dclk2.clock.asUInt
    port.sys_rst := shell.pllReset
    port.aresetn := !(ar.reset.asBool)
  } }

  shell.sdc.addGroup(clocks = Seq("clk_pll_i"), pins = Seq(mig.island.module.blackbox.io.ui_clk))
}
class DDRSophiaLakeShellPlacer(val shell: SophiaLakeShellBasicOverlays, val shellInput: DDRShellInput)(implicit val valName: ValName)
  extends DDRShellPlacer[SophiaLakeShellBasicOverlays] {
  def place(designInput: DDRDesignInput) = new DDRSophiaLakePlacedOverlay(shell, valName.name, designInput, shellInput)
}


abstract class SophiaLakeShellBasicOverlays()(implicit p: Parameters) extends Series7Shell {
  // Order matters; ddr depends on sys_clock
  val sys_clock = Overlay(ClockInputOverlayKey, new SysClockSophiaLakeShellPlacer(this, ClockInputShellInput()))
  val led       = Seq.tabulate(4)(i => Overlay(LEDOverlayKey, new LEDSophiaLakeShellPlacer(this, LEDMetas(i))(valName = ValName(s"led_$i"))))
  val button    = Seq.tabulate(2)(i => Overlay(ButtonOverlayKey, new ButtonSophiaLakeShellPlacer(this, ButtonShellInput(number = i))(valName = ValName(s"button_$i"))))
  val ddr       = Overlay(DDROverlayKey, new DDRSophiaLakeShellPlacer(this, DDRShellInput()))
  val cts_reset = Overlay(CTSResetOverlayKey, new CTSResetSophiaLakeShellPlacer(this, CTSResetShellInput()))

  def LEDMetas(i: Int): LEDShellInput =
    LEDShellInput(
      color = if((i < 12) && (i % 3 == 1)) "green" else if((i < 12) && (i % 3 == 2)) "blue" else "red",
      rgb = (i < 12),
      number = i)
}

class SophiaLakeShell()(implicit p: Parameters) extends SophiaLakeShellBasicOverlays
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
    xdc.addPackagePin(reset, "AB22")
    xdc.addIOStandard(reset, "LVCMOS33")
    xdc.addIOB(reset)
    xdc.addPulldown(reset)

    val reset_ibuf = Module(new IBUF)
    reset_ibuf.io.I := reset
    val sysclk: Clock = sys_clock.get() match {
      case Some(x: SysClockSophiaLakePlacedOverlay) => x.clock
    }
    val powerOnReset = PowerOnResetFPGAOnly(sysclk)
    sdc.addAsyncPath(Seq(powerOnReset))

    resetPin := ~reset_ibuf.io.O

    withClockAndReset(sysclk, false.B) {
      pllReset := SyncResetSynchronizerShiftReg((reset_ibuf.io.O) || powerOnReset , 2, init = true.B, name=Some("reset_pll_sync")) //SophiaLake is active low reset
    }

    
      
  }
}
