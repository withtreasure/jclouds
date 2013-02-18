/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.scriptbuilder.statements.chef;

import static org.jclouds.scriptbuilder.domain.Statements.exec;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.scriptbuilder.statements.ruby.InstallRuby;

/**
 * Installs Chef gems onto a host.
 * 
 * @author Ignasi Barrera
 */
public class InstallChefGems extends StatementList {

   public InstallChefGems() {
      // Chef versions prior to 10.16.4 install an incompatible moneta gem.
      // See: http://tickets.opscode.com/browse/CHEF-3721
      super(new InstallRuby(), exec("gem install chef -v '>= 10.16.4' --no-rdoc --no-ri"));
   }

   @Override
   public String render(OsFamily family) {
      if (family == OsFamily.WINDOWS) {
         throw new UnsupportedOperationException("windows not yet implemented");
      }
      return super.render(family);
   }

}
