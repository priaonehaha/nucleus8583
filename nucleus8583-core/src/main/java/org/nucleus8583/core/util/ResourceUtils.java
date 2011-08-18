package org.nucleus8583.core.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ResourceUtils {
	private static final String LOCATION_PREFIX_FILE = "file:";
	private static final String LOCATION_PREFIX_CLASSPATH = "classpath:";

	private static void addResources(List<URL> resolved,
			Set<String> doubleChecker, ClassLoader cl, String name) {
		Enumeration<URL> en = null;

		try {
			if (cl == null) {
				en = ClassLoader.getSystemResources(name);
			} else {
				en = cl.getResources(name);
			}
		} catch (Throwable t) {
			// do nothing
		}

		if (en != null) {
			while (en.hasMoreElements()) {
				URL el = en.nextElement();
				String strEl = el.toString();

				if (!doubleChecker.contains(strEl)) {
					resolved.add(el);
					doubleChecker.add(strEl);
				}
			}
		}
	}

	public static URL[] getURLs(String location) {
		List<URL> resolved = new ArrayList<URL>();
		Set<String> doubleChecker = new HashSet<String>();

		if (location.startsWith(ResourceUtils.LOCATION_PREFIX_CLASSPATH)) {
			location = location.substring(10);

			try {
				addResources(resolved, doubleChecker, Thread.currentThread()
						.getContextClassLoader(), location);
			} catch (Throwable t) {
				// do nothing
			}

			try {
				addResources(resolved, doubleChecker,
						ResourceUtils.class.getClassLoader(), location);
			} catch (Throwable t) {
				// do nothing
			}

			try {
				addResources(resolved, doubleChecker,
						ClassLoader.getSystemClassLoader(), location);
			} catch (Throwable t) {
				// do nothing
			}

			try {
				addResources(resolved, doubleChecker, null, location);
			} catch (Throwable t) {
				// do nothing
			}
		} else {
			URL resolved1;

			try {
				resolved1 = new URL(location);
			} catch (MalformedURLException ex) {
				if (location.startsWith(ResourceUtils.LOCATION_PREFIX_FILE))
					location = location.substring(5);

				try {
					resolved1 = new File(location).toURI().toURL();
				} catch (MalformedURLException ex2) {
					throw new RuntimeException(new FileNotFoundException(
							"unable to find " + location));
				}
			}

			if (resolved1 != null) {
				resolved.add(resolved1);
			}
		}

		return resolved.toArray(new URL[0]);
	}

	public static URL getURL(String location) {
		URL resolved = null;

		if (location.startsWith(ResourceUtils.LOCATION_PREFIX_CLASSPATH)) {
			location = location.substring(10);

			try {
				resolved = Thread.currentThread().getContextClassLoader()
						.getResource(location);
			} catch (Throwable t) {
				// do nothing
			}

			if (resolved == null) {
				try {
					resolved = ResourceUtils.class.getResource(location);
				} catch (Throwable t) {
					// do nothing
				}
			}

			if (resolved == null) {
				try {
					resolved = ClassLoader.getSystemClassLoader().getResource(
							location);
				} catch (Throwable t) {
					// do nothing
				}
			}

			if (resolved == null) {
				try {
					resolved = ClassLoader.getSystemResource(location);
				} catch (Throwable t) {
					// do nothing
				}
			}
		} else {
			try {
				resolved = new URL(location);
			} catch (MalformedURLException ex) {
				if (location.startsWith(ResourceUtils.LOCATION_PREFIX_FILE))
					location = location.substring(5);

				try {
					resolved = new File(location).toURI().toURL();
				} catch (MalformedURLException ex2) {
					throw new RuntimeException(new FileNotFoundException(
							"unable to find " + location));
				}
			}
		}

		return resolved;
	}

	public static ClassLoader getDefaultClassLoader() {
	    ClassLoader cl = null;

        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Throwable t) {
            // do nothing
        }

        if (cl == null) {
            cl = ResourceUtils.class.getClassLoader();
        }

        return cl;
	}
}
