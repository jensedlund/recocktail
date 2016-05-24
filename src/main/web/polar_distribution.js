/*
 * Copyright 2016 Jens Edlund, Joakim Gustafson, Jonas Beskow, Ulrika Goloconda Fahlen, Jan Eriksson, Marcus Viden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @version 1.0
 * @since 2016-04-11
 * 
 */

// Javascript verion of Java code from wikipedia on the Marsaglia polar
// method to creates a pseudo random normal (Gaussian) distribution.
function PolarDistribution() {
    this.spare = 0;
    this.isSpareReady = false;
}

PolarDistribution.prototype.getGaussian = function(mean, stdDev) {
    if (this.isSpareReady) {
        this.isSpareReady = false;
        return (this.spare * stdDev) + mean;
    } else {
        var u, v, s;
        do {
            u = Math.random() * 2 - 1;
            v = Math.random() * 2 - 1;
            s = u * u + v * v;
        } while (s >= 1 || s == 0);
        var mul = Math.sqrt(-2.0 * Math.log(s) / s);
        this.spare = v * mul;
        this.isSpareReady = true;
        return mean + (stdDev * u * mul);
    }
};