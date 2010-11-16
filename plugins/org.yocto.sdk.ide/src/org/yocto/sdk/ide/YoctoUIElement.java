/*******************************************************************************
 * Copyright (c) 2010 Intel Corporation.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intel - initial API and implementation
 *******************************************************************************/
package org.yocto.sdk.ide;

public class YoctoUIElement {
	public enum PokyMode
	{
		POKY_SDK_MODE,
		POKY_TREE_MODE
	};
	public enum DeviceMode
	{
		QEMU_MODE,
		DEVICE_MODE
	};

	private String strTarget;
	private String[] strTargetsArray;
	private DeviceMode enumDeviceMode;
	private String strQemuKernelLoc;
	private String strQemuRootFSLoc;
	private PokyMode enumPokyMode;
	private String strToolChainRoot;
	private int intTargetIndex;	

	public YoctoUIElement()
	{
		this.enumDeviceMode = DeviceMode.QEMU_MODE;
		this.enumPokyMode = PokyMode.POKY_SDK_MODE;
		this.strToolChainRoot = "";
		this.strQemuKernelLoc = "";
		this.strQemuRootFSLoc = "";
		this.intTargetIndex = -1;
		this.strTarget = "";
	}

	public PokyMode getEnumPokyMode() {
		return enumPokyMode;
	}
	public void setEnumPokyMode(PokyMode enumPokyMode) {
		this.enumPokyMode = enumPokyMode;
	}
	public String getStrToolChainRoot() {
		return strToolChainRoot;
	}
	public void setStrToolChainRoot(String strToolChainRoot) {
		this.strToolChainRoot = strToolChainRoot;
	}
	public int getIntTargetIndex() {
		if ((this.strTargetsArray != null) && (this.strTargetsArray.length == 1))
			return 0;
		return intTargetIndex;
	}
	public void setIntTargetIndex(int intTargetIndex) {
		this.intTargetIndex = intTargetIndex;
	}
	public String getStrTarget() {
		return strTarget;
	}
	public void setStrTarget(String strTarget) {
		this.strTarget = strTarget;
	}
	public String[] getStrTargetsArray() {
		return strTargetsArray;
	}
	public void setStrTargetsArray(String[] strTargetsArray) {
		this.strTargetsArray = strTargetsArray;
	}
	public DeviceMode getEnumDeviceMode() {
		return enumDeviceMode;
	}
	public void setEnumDeviceMode(DeviceMode enumDeviceMode) {
		this.enumDeviceMode = enumDeviceMode;
	}
	public String getStrQemuKernelLoc() {
		return strQemuKernelLoc;
	}
	public void setStrQemuKernelLoc(String strQemuKernelLoc) {
		this.strQemuKernelLoc = strQemuKernelLoc;
	}
	public String getStrQemuRootFSLoc() {
		return strQemuRootFSLoc;
	}
	public void setStrQemuRootFSLoc(String strQemuRootFSLoc) {
		this.strQemuRootFSLoc = strQemuRootFSLoc;
	}
}
