package com.kitakeyos.data;

import com.kitakeyos.object.Skill;
import com.kitakeyos.template.SkillOptionTemplate;
import com.kitakeyos.template.SkillTemplate;
import java.util.HashMap;

public class SkillData {
	public static HashMap<Integer, NClass> nClasss;
	public static HashMap<Integer, SkillOptionTemplate> optionTemplates;

	public static Skill getSkill(int classId, int templateId, int point) {
		NClass n = nClasss.get(Integer.valueOf(classId));
		if (n != null) {
			SkillTemplate tem = n.templates.get(Integer.valueOf(templateId));
			if (tem != null) {
				for (Skill skill : tem.skills) {
					if (skill.point == point) {
						return skill;
					}
				}
			}
		}
		return null;
	}

	public static SkillTemplate getTemplate(int classId, int templateId) {
		NClass n = nClasss.get(Integer.valueOf(classId));
		if (n != null) {
			SkillTemplate tem = n.templates.get(Integer.valueOf(templateId));
			if (tem != null) {
				return tem;
			}
		}

		return null;
	}

	public static class NClass {
		public int classId;
		public String name;
		public HashMap<Integer, SkillTemplate> templates;
	}
}

/*
 * Location:
 * C:\Users\Administrator\Downloads\SerNinjaNN2\dist\ninja.jar!\com\kitakeyos\
 * data\SkillData.class Java compiler version: 8 (52.0) JD-Core Version: 1.1.3
 */