package sifive.fpgashells.ip.xilinx.ibufds_gte2

import Chisel._

//IP : xilinx unisim IBUFDS_GTE2
//Differential Signaling Input Buffer
//unparameterized

class IBUFDS_GTE2 extends BlackBox {
  val io = new Bundle {
    val O         = Bool(OUTPUT)
    val ODIV2     = Bool(OUTPUT)
    val CEB       = Bool(INPUT)
    val I         = Bool(INPUT)
    val IB        = Bool(INPUT)
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
